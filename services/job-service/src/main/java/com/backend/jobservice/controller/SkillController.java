package com.backend.jobservice.controller;

import com.backend.jobservice.dto.request.CreateSkillRequest;
import com.backend.jobservice.dto.request.UpdateSkillRequest;
import com.backend.jobservice.dto.response.ApiResponse;
import com.backend.jobservice.dto.response.ListDataRes;
import com.backend.jobservice.dto.response.SkillResponse;
import com.backend.jobservice.enums.SuccessCode;
import com.backend.jobservice.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skill/v1/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(@RequestBody @Valid CreateSkillRequest request) {
        SkillResponse result = skillService.createSkill(request);
        return ResponseEntity
                .status(SuccessCode.SKILL_CREATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SKILL_CREATED.getCode(),
                        SuccessCode.SKILL_CREATED.getMessage(),
                        result
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(@PathVariable("id") UUID id, @RequestBody @Valid UpdateSkillRequest request) {
        SkillResponse result = skillService.updateSkill(id, request);
        return ResponseEntity
                .status(SuccessCode.SKILL_UPDATED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SKILL_UPDATED.getCode(),
                        SuccessCode.SKILL_UPDATED.getMessage(),
                        result
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> getSkill(@PathVariable("id") UUID id) {
        SkillResponse result = skillService.getSkill(id);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ListDataRes<SkillResponse>>> searchSkills(
            @RequestParam(value = "q", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "1000") int size,
            @RequestParam(value = "categoryId", required = false) UUID categoryId) {
        ListDataRes<SkillResponse> result = (size >= 1000 && categoryId == null)
                ? skillService.searchSkills(keyword)
                : skillService.searchSkills(keyword, page, size, categoryId);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ListDataRes<SkillResponse>>> getAllSkills(
            @RequestParam(value = "q", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "1000") int size,
            @RequestParam(value = "categoryId", required = false) UUID categoryId) {
        ListDataRes<SkillResponse> result = (size >= 1000 && categoryId == null)
                ? skillService.searchSkills(keyword)
                : skillService.searchSkills(keyword, page, size, categoryId);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<ListDataRes<SkillResponse>>> getSkillsByCategory(
            @PathVariable("categoryId") UUID categoryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "1000") int size) {
        ListDataRes<SkillResponse> result = (size >= 1000)
                ? skillService.getSkillsByCategory(categoryId)
                : skillService.searchSkills("", page, size, categoryId);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable("id") UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity
                .status(SuccessCode.SKILL_DELETED.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.SKILL_DELETED.getCode(),
                        SuccessCode.SKILL_DELETED.getMessage(),
                        null
                ));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getSkillsBatch(@RequestBody List<UUID> ids) {
        List<SkillResponse> result = skillService.getSkillsByIds(ids);
        return ResponseEntity
                .status(SuccessCode.GET_SUCCESS.getStatus())
                .body(ApiResponse.success(
                        SuccessCode.GET_SUCCESS.getCode(),
                        SuccessCode.GET_SUCCESS.getMessage(),
                        result
                ));
    }
}
