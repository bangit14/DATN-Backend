package com.backend.candidateservice.cv.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.candidateservice.cv.client.AiNlpClient;
import com.backend.candidateservice.cv.client.ProfileClient;
import com.backend.candidateservice.cv.core.enums.FilterOpEnum;
import com.backend.candidateservice.cv.core.utils.PropertyColumnUtil;
import com.backend.candidateservice.cv.dto.*;
import com.backend.candidateservice.cv.dto.response.ListDataRes;
import com.backend.candidateservice.cv.entity.CV;
import com.backend.candidateservice.cv.entity.CvNorm;
import com.backend.candidateservice.cv.exception.ResourceNotFoundException;
import com.backend.candidateservice.cv.mapper.db.CVDbMapper;
import com.backend.candidateservice.cv.mapper.db.CvNormDbMapper;
import com.backend.candidateservice.cv.service.CvNormService;
import com.backend.candidateservice.cv.service.CvService;
import com.backend.candidateservice.cv.service.S3FileStorageService;
import com.backend.candidateservice.cv.util.CvTextExtractor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CvServiceImpl extends ServiceImpl<CVDbMapper, CV> implements CvService {
    private final CVDbMapper cvDbMapper;
    private final CvNormDbMapper cvNormDbMapper;
    private final S3FileStorageService s3FileStorageService;
    private final CvNormService cvNormService;
    private final AiNlpClient aiNlpClient;
    private final ProfileClient profileClient;

    private static final Logger log = LoggerFactory.getLogger(CvServiceImpl.class);

    @Override
    @Transactional
    public CvSummaryDto uploadExtractAndSaveCv(UUID studentId, String cvName, MultipartFile file) {
        String fileKey;
        String rawText;
        try {
            rawText = CvTextExtractor.extractText(
                    file.getInputStream(),
                    file.getOriginalFilename()
            );
            fileKey = s3FileStorageService.storeFileKey(file);
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
        newCv.setCvUrl(fileKey);
        newCv.setDefault(isFirstCv);
        newCv.setRawText(rawText);
        newCv.setNlpStatus("PENDING");

        cvDbMapper.insert(newCv);

        try {
            log.info("Đang cập nhật CV URL cho student {} sang Profile Service...", studentId);
            profileClient.updateCvUrl(studentId, fileKey);
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
                .cvUrl(s3FileStorageService.presignedUrl(cv.getCvUrl()))
                .isDefault(cv.isDefault())
                .build();
    }

    private CvDetailDto mapToCvDetailDto(CV cv) {
        return CvDetailDto.builder()
                .id(cv.getId())
                .cvName(cv.getCvName())
                .cvUrl(s3FileStorageService.presignedUrl(cv.getCvUrl()))
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

    @Override
    @Transactional(readOnly = true)
    public ListDataRes<EmployerCvResponse> filterCvsForEmployer(CvPageRequest request) {
        if (request == null) {
            request = new CvPageRequest();
        }

        MappedCvQuery query = buildMappedCvQuery(request);

        Page<CV> page = new Page<>(query.getPageIndex(), query.getPageSize());

        List<CV> cvList = cvDbMapper.getCvPageForEmployer(page, query);

        if (!cvList.isEmpty()) {
            List<Long> cvIds = cvList.stream().map(CV::getId).collect(Collectors.toList());
            List<CvNorm> norms = cvNormDbMapper.selectBatchIds(cvIds);
            if (norms != null && !norms.isEmpty()) {
                Map<Long, CvNorm> normMap = norms.stream()
                        .collect(Collectors.toMap(CvNorm::getCvId, n -> n, (a, b) -> a));
                for (CV cv : cvList) {
                    cv.setCvNorm(normMap.get(cv.getId()));
                }
            }
        }

        List<EmployerCvResponse> responses = cvList.stream()
                .map(this::mapToEmployerCvResponse)
                .collect(Collectors.toList());

        return new ListDataRes<>(responses, page);
    }

    private EmployerCvResponse mapToEmployerCvResponse(CV cv) {
        if (cv == null) return null;
        EmployerCvResponse.EmployerCvResponseBuilder builder = EmployerCvResponse.builder()
                .id(cv.getId())
                .studentId(cv.getStudentId())
                .cvName(cv.getCvName())
                .templateId(cv.getTemplateId())
                .cvUrl(s3FileStorageService.presignedUrl(cv.getCvUrl()))
                .pdfUrl(s3FileStorageService.presignedUrl(cv.getPdfUrl()))
                .isDefault(cv.isDefault())
                .rawText(cv.getRawText())
                .nlpStatus(cv.getNlpStatus())
                .processedAt(cv.getProcessedAt())
                .createdAt(cv.getCreatedAt())
                .updatedAt(cv.getUpdatedAt());

        CvNorm norm = cv.getCvNorm();
        if (norm != null) {
            builder.yearsTotal(norm.getYearsTotal())
                    .educationLevel(norm.getEducationLevel())
                    .modelVersion(norm.getModelVersion())
                    .skillsNorm(norm.getSkillsNorm())
                    .experienceTitles(norm.getExperienceTitles())
                    .experienceAreas(norm.getExperienceAreas())
                    .educationMajors(norm.getEducationMajors());
        }

        return builder.build();
    }

    private MappedCvQuery buildMappedCvQuery(CvPageRequest request) {
        List<GroupMappedCvFieldFilter> mappedGroups = mapGroupFilters(request.getCvFieldFilter());

        MappedCvQuery query = new MappedCvQuery();
        query.setPageIndex(request.getPageIndex());
        query.setPageSize(request.getPageSize());
        query.setKeyword(request.getKeyword() != null ? request.getKeyword().trim() : null);
        query.setStudentId(request.getStudentId());
        query.setIsDefault(request.getIsDefault());
        query.setNlpStatus(request.getNlpStatus() != null ? request.getNlpStatus().trim() : null);
        query.setEducationLevel(request.getEducationLevel() != null ? request.getEducationLevel().trim() : null);
        query.setYearsTotalMin(request.getYearsTotalMin());
        query.setYearsTotalMax(request.getYearsTotalMax());
        query.setCvIds(request.getCvIds());
        query.setFilters(mappedGroups);
        query.setHaveFilter(!mappedGroups.isEmpty());

        if (request.getSortBy() != null && !request.getSortBy().isBlank()) {
            String sortField = request.getSortBy().trim();
            String alias = isNormField(sortField) ? "t1." : "t0.";
            Class<?> entityClass = isNormField(sortField) ? CvNorm.class : CV.class;
            String sortCol = PropertyColumnUtil.getColumn(entityClass, sortField);
            if (sortCol == null && sortField.matches("^[a-zA-Z0-9_]+$")) {
                sortCol = sortField;
            }
            if (sortCol != null) {
                query.setSortBy(alias + sortCol);
                query.setSortDirection(request.getSortDirection() != null ? request.getSortDirection().getDisplayName() : "DESC");
            }
        }

        return query;
    }

    private List<GroupMappedCvFieldFilter> mapGroupFilters(List<List<CvFieldFilter>> rawGroups) {
        if (rawGroups == null || rawGroups.isEmpty()) {
            return Collections.emptyList();
        }

        List<GroupMappedCvFieldFilter> mappedGroups = new ArrayList<>();
        for (List<CvFieldFilter> orGroup : rawGroups) {
            if (orGroup == null || orGroup.isEmpty()) continue;

            List<MappedCvFieldFilter> andFilters = new ArrayList<>();
            for (CvFieldFilter filter : orGroup) {
                MappedCvFieldFilter mapped = mapFieldFilter(filter);
                if (mapped != null) {
                    andFilters.add(mapped);
                }
            }

            if (!andFilters.isEmpty()) {
                GroupMappedCvFieldFilter group = new GroupMappedCvFieldFilter();
                group.setFilters(andFilters);
                mappedGroups.add(group);
            }
        }
        return mappedGroups;
    }

    private MappedCvFieldFilter mapFieldFilter(CvFieldFilter filter) {
        if (filter == null || filter.getField() == null || filter.getField().isBlank()
                || filter.getValue() == null || filter.getValue().isBlank()) {
            return null;
        }

        String fieldName = filter.getField().trim();
        boolean isNorm = isNormField(fieldName);
        String alias = isNorm ? "t1." : "t0.";
        Class<?> entityClass = isNorm ? CvNorm.class : CV.class;

        String col = PropertyColumnUtil.getColumn(entityClass, fieldName);
        if (col == null || col.isBlank()) {
            if (fieldName.matches("^[a-zA-Z0-9_]+$")) {
                col = fieldName;
            } else {
                return null;
            }
        }

        FilterOpEnum op = (filter.getOp() != null) ? filter.getOp() : FilterOpEnum.EQUAL;
        String val = filter.getValue().trim();

        MappedCvFieldFilter r = new MappedCvFieldFilter();
        r.setAlias(alias);
        r.setField(col);
        r.setOp(" " + op.getSqlOp() + " ");

        if (op == FilterOpEnum.CONTAINS || op == FilterOpEnum.DO_NOT_CONTAIN) {
            if (!val.startsWith("%") && !val.endsWith("%")) {
                val = "%" + val + "%";
            }
            r.setValue(val);
            r.setListValue(false);
        } else if (op == FilterOpEnum.IN || op == FilterOpEnum.NOT_IN) {
            String[] tokens = val.replace("[", "").replace("]", "").split(",");
            List<String> elList = new ArrayList<>();
            for (String t : tokens) {
                String trimmed = t.trim().replace("\"", "").replace("'", "");
                if (!trimmed.isEmpty()) {
                    elList.add(trimmed);
                }
            }
            r.setValue(elList);
            r.setListValue(true);
        } else {
            r.setValue(val);
            r.setListValue(false);
        }

        return r;
    }

    private boolean isNormField(String field) {
        if (field == null) return false;
        String f = field.toLowerCase();
        return f.contains("year") || f.contains("education") || f.contains("model");
    }
}
