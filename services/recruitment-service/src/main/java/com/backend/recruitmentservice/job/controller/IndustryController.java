package com.backend.recruitmentservice.job.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.recruitmentservice.job.dto.response.ApiResponse;
import com.backend.recruitmentservice.job.dto.response.IndustryResponse;
import com.backend.recruitmentservice.job.entity.Industry;
import com.backend.recruitmentservice.job.mapper.db.IndustryDbMapper;
import com.backend.recruitmentservice.job.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/industries")
@RequiredArgsConstructor
public class IndustryController {

    private final IndustryDbMapper industryDbMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IndustryResponse>>> getIndustries() {
        List<IndustryResponse> result = industryDbMapper.selectList(
                        new LambdaQueryWrapper<Industry>().orderByAsc(Industry::getName)
                ).stream()
                .map(industry -> IndustryResponse.builder()
                        .id(industry.getId())
                        .name(industry.getName())
                        .slug(industry.getSlug())
                        .build())
                .toList();

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCode.GET_SUCCESS.getCode(),
                "Industries fetched successfully",
                result
        ));
    }
}
