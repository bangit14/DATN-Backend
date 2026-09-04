package com.backend.cv_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.backend.cv_service.client.AiNlpClient;
import com.backend.cv_service.client.ProfileClient;
import com.backend.cv_service.dto.*;
import com.backend.cv_service.entity.CV;
import com.backend.cv_service.exception.ResourceNotFoundException;
import com.backend.cv_service.mapper.db.CVDbMapper;
import com.backend.cv_service.service.CvNormService;
import com.backend.cv_service.service.CvService;
import com.backend.cv_service.service.S3FileStorageService;
import com.backend.cv_service.util.CvTextExtractor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
public class CvServiceImpl extends ServiceImpl<CVDbMapper, CV> implements CvService {
    private final CVDbMapper cvDbMapper;
    private final S3FileStorageService s3FileStorageService;
    private final CvNormService cvNormService;
    private final AiNlpClient aiNlpClient;
    private final ProfileClient profileClient;

    private static final Logger log = LoggerFactory.getLogger(CvServiceImpl.class);

    @Override
    @Transactional
    public CvSummaryDto uploadExtractAndSaveCv(UUID studentId, String cvName, MultipartFile file) {
        String fileUrl;
        String rawText;
        try {
            rawText = CvTextExtractor.extractText(
                    file.getInputStream(),
                    file.getOriginalFilename()
            );
            fileUrl = s3FileStorageService.storeFile(file, "cvs/");
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + file.getOriginalFilename(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi trích xuất nội dung CV: " + file.getOriginalFilename(), e);
        }

        Long existingCount = cvDbMapper.selectCount(
                new LambdaQueryWrapper<CV>().eq(CV::getStudentId, studentId)
        );
        boolean isFirstCv = (existingCount == null || existingCount == 0);

        CV newCv = new CV();
        newCv.setStudentId(studentId);
        newCv.setCvName(cvName);
        newCv.setCvUrl(fileUrl);
        newCv.setDefault(isFirstCv);
        newCv.setRawText(rawText);
        newCv.setNlpStatus("PENDING");

        cvDbMapper.insert(newCv);

        try {
            log.info("Đang cập nhật CV URL cho student {} sang Profile Service...", studentId);
            profileClient.updateCvUrl(studentId, fileUrl);
        } catch (Exception e) {
            log.error("Lỗi khi gọi Profile Service để update CV URL: {}", e.getMessage());
        }

        try {
            CvNlpRequest request = new CvNlpRequest();
            request.setCvId(newCv.getId());
            request.setRawText(rawText);

            CvNlpResultDto nlpResult = aiNlpClient.processCv(request);

            cvNormService.upsertFromNlpResult(newCv.getId(), nlpResult);

            newCv.setNlpStatus("SUCCESS");
            newCv.setProcessedAt(OffsetDateTime.now());
            cvDbMapper.updateById(newCv);

        } catch (Exception ex) {
            log.error("Lỗi khi xử lý NLP cho CV {}: {}", newCv.getId(), ex.getMessage());

            newCv.setNlpStatus("FAILED");
            cvDbMapper.updateById(newCv);
        }

        return mapToCvSummaryDto(newCv);
    }

    private CvSummaryDto mapToCvSummaryDto(CV cv) {
        return CvSummaryDto.builder()
                .id(cv.getId())
                .cvName(cv.getCvName())
                .cvUrl(cv.getCvUrl())
                .isDefault(cv.isDefault())
                .build();
    }

    private CvDetailDto mapToCvDetailDto(CV cv) {
        return CvDetailDto.builder()
                .id(cv.getId())
                .cvName(cv.getCvName())
                .cvUrl(cv.getCvUrl())
                .isDefault(cv.isDefault())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CvSummaryDto> findAllByStudentId(UUID studentId) {
        List<CV> cvs = cvDbMapper.selectList(
                new LambdaQueryWrapper<CV>().eq(CV::getStudentId, studentId)
        );
        return cvs.stream()
                .map(this::mapToCvSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CvDetailDto findCvDetailById(Long cvId, UUID studentId) {
        CV cv = cvDbMapper.selectOne(
                new LambdaQueryWrapper<CV>()
                        .eq(CV::getId, cvId)
                        .eq(CV::getStudentId, studentId)
        );
        if (cv == null) {
            throw new ResourceNotFoundException("Không tìm thấy CV hoặc bạn không có quyền xem CV này.");
        }
        return mapToCvDetailDto(cv);
    }

    @Override
    @Transactional
    public CvSummaryDto updateCvName(Long cvId, UUID studentId, String newCvName) {
        CV cv = cvDbMapper.selectById(cvId);
        if (cv == null) {
            throw new ResourceNotFoundException("Không tìm thấy CV");
        }
        if (!cv.getStudentId().equals(studentId)) {
            throw new SecurityException("CV này không phải của bạn!");
        }
        cv.setCvName(newCvName);
        cvDbMapper.updateById(cv);
        return mapToCvSummaryDto(cv);
    }

    @Override
    @Transactional
    public void deleteCv(Long cvId, UUID studentId) {
        CV cv = cvDbMapper.selectOne(
                new LambdaQueryWrapper<CV>()
                        .eq(CV::getId, cvId)
                        .eq(CV::getStudentId, studentId)
        );
        if (cv == null) {
            throw new ResourceNotFoundException("Không tìm thấy CV hoặc bạn không có quyền xóa CV này.");
        }

        try {
            s3FileStorageService.deleteFile(cv.getCvUrl());
        } catch (Exception e) {
            log.warn("Không thể xóa file trên S3: {}", cv.getCvUrl(), e);
        }

        cvDbMapper.deleteById(cvId);
    }

    @Override
    @Transactional
    public void setDefaultCv(Long cvId, UUID studentId) {
        // Reset all student's CVs to not default
        cvDbMapper.update(
                null,
                new LambdaUpdateWrapper<CV>()
                        .set(CV::isDefault, false)
                        .eq(CV::getStudentId, studentId)
        );

        CV cv = cvDbMapper.selectOne(
                new LambdaQueryWrapper<CV>()
                        .eq(CV::getId, cvId)
                        .eq(CV::getStudentId, studentId)
        );
        if (cv == null) {
            throw new ResourceNotFoundException("CV không tồn tại");
        }
        cv.setDefault(true);
        cvDbMapper.updateById(cv);
    }
}
