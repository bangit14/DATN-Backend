package com.backend.candidateservice.profile.controller;

import com.backend.candidateservice.profile.dto.skill.CreateSkillRequest;
import com.backend.candidateservice.profile.dto.skill.SkillResponse;
import com.backend.candidateservice.profile.dto.skill.UpdateSkillRequest;
import com.backend.candidateservice.profile.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<SkillResponse> createSkill(@RequestBody @Valid CreateSkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<SkillResponse> updateSkill(@PathVariable("id") UUID id, @RequestBody @Valid UpdateSkillRequest request) {
        return ResponseEntity.ok(skillService.updateSkill(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getSkill(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(skillService.getSkill(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SkillResponse>> searchSkills(@RequestParam(name = "name", required = false) String name) {
        return ResponseEntity.ok(skillService.searchSkills(name));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SkillResponse>> getSkillsByCategory(@PathVariable("categoryId") UUID categoryId) {
        return ResponseEntity.ok(skillService.getSkillsByCategory(categoryId));
    }

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN') or hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
