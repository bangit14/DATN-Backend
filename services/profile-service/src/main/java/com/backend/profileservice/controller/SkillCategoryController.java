package com.backend.profileservice.controller;

import com.backend.profileservice.dto.skill.CreateCategoryRequest;
import com.backend.profileservice.dto.skill.SkillCategoryResponse;
import com.backend.profileservice.dto.skill.UpdateCategoryRequest;
import com.backend.profileservice.service.SkillCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/categories")
@RequiredArgsConstructor
public class SkillCategoryController {

    private final SkillCategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<SkillCategoryResponse> createCategory(@RequestBody @Valid CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<SkillCategoryResponse> updateCategory(@PathVariable("id") UUID id, @RequestBody @Valid UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillCategoryResponse> getCategory(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @GetMapping
    public ResponseEntity<List<SkillCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
