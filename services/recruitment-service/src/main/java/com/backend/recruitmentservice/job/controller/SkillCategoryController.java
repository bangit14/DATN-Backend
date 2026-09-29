package com.backend.recruitmentservice.job.controller;

import com.backend.recruitmentservice.job.dto.request.CreateCategoryRequest;
import com.backend.recruitmentservice.job.dto.request.UpdateCategoryRequest;
import com.backend.recruitmentservice.job.dto.response.ApiResponse;
import com.backend.recruitmentservice.job.dto.response.ListDataRes;
import com.backend.recruitmentservice.job.dto.response.SkillCategoryResponse;
import com.backend.recruitmentservice.job.dto.response.SkillResponse;
import com.backend.recruitmentservice.job.enums.SuccessCode;
import com.backend.recruitmentservice.job.service.SkillCategoryService;
import com.backend.recruitmentservice.job.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/skill/v1/categories")
@RequiredArgsConstructor
public class SkillCategoryController {

    private final SkillCategoryService categoryService;
    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<ApiResponse<SkillCategoryResponse>> createCategory(@RequestBody @Valid CreateCategoryRequest request) {
        SkillCategoryResponse result = categoryService.createCategory(request);
        return ResponseEntity
                .status(SuccessCode.CATEGORY_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.CATEGORY_CREATED.getCode(),
                        SuccessCode.CATEGORY_CREATED.getMessage(),
                        result
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillCategoryResponse>> updateCategory(@PathVariable("id") UUID id,
                                                                             @RequestBody @Valid UpdateCategoryRequest request) {
        SkillCategoryResponse result = categoryService.updateCategory(id, request);
        return ResponseEntity
                .status(SuccessCode.CATEGORY_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.CATEGORY_UPDATED.getCode(),
                        SuccessCode.CATEGORY_UPDATED.getMessage(),
                        result
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ListDataRes<SkillCategoryResponse>>> getAllCategories(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "1000") int size,
            @RequestParam(value = "q", required = false, defaultValue = "") String keyword
    ) {
        ListDataRes<SkillCategoryResponse> result = (size >= 1000 && keyword.isBlank())
                ? categoryService.getAllCategories()
                : categoryService.getCategoriesPage(page, size, keyword);

        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillCategoryResponse>> getCategory(@PathVariable("id") UUID id) {
        SkillCategoryResponse result = categoryService.getCategory(id);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<ApiResponse<ListDataRes<SkillResponse>>> getSkillsByCategory(
            @PathVariable("id") UUID id,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "1000") int size
    ) {
        ListDataRes<SkillResponse> result = (size >= 1000)
                ? skillService.getSkillsByCategory(id)
                : skillService.searchSkills("", page, size, id);

        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity
                .status(SuccessCode.CATEGORY_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.CATEGORY_DELETED.getCode(),
                        SuccessCode.CATEGORY_DELETED.getMessage(),
                        null
                ));
    }
}
