package com.backend.candidateservice.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.candidateservice.profile.core.base.BaseService;
import com.backend.candidateservice.profile.dto.request.CompanyCreateRequest;
import com.backend.candidateservice.profile.dto.request.CompanyUpdateRequest;
import com.backend.candidateservice.profile.dto.response.CompanyBasicResponse;
import com.backend.candidateservice.profile.dto.response.CompanyResponse;
import com.backend.candidateservice.profile.entity.Company;
import com.backend.candidateservice.profile.entity.Employer;
import com.backend.candidateservice.profile.enums.ErrorCode;
import com.backend.candidateservice.profile.exception.AppException;
import com.backend.candidateservice.profile.mapper.CompanyMapper;
import com.backend.candidateservice.profile.mapper.db.CompanyDbMapper;
import com.backend.candidateservice.profile.mapper.db.EmployerDbMapper;
import com.backend.candidateservice.profile.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl extends BaseService<CompanyDbMapper, Company> implements CompanyService {

    private final CompanyDbMapper companyDbMapper;
    private final EmployerDbMapper employerDbMapper;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
    public CompanyResponse create(UUID userId, CompanyCreateRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        log.info("Creating company name={} by employer userId={}", request.getName(), userId);

        boolean exists = companyDbMapper.exists(
                new LambdaQueryWrapper<Company>().eq(Company::getName, request.getName().trim())
        );
        if (exists) {
            throw new AppException(ErrorCode.COMPANY_NAME_EXISTED);
        }

        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null) {
            throw new AppException(ErrorCode.EMPLOYER_NOT_FOUND);
        }

        if (employer.getCompanyId() != null) {
            throw new AppException(ErrorCode.EMPLOYER_ALREADY_HAS_COMPANY);
        }

        Company company = companyMapper.toEntity(request);
        if (company.getId() == null) {
            company.setId(UUID.randomUUID());
        }
        company.setSlug(buildSlug(request.getName(), company.getId()));
        company.setTaxCode(clean(request.getTaxCode()) != null
                ? clean(request.getTaxCode())
                : generateTaxCode(company.getId()));
        company.setStatus("ACTIVE");
        if (company.getCreatedAt() == null) {
            company.setCreatedAt(Instant.now());
        }
        if (company.getUpdatedAt() == null) {
            company.setUpdatedAt(Instant.now());
        }
        companyDbMapper.insert(company);

        employer.setCompanyId(company.getId());
        employer.setCompany(company);
        employer.setAdmin(true);
        employerDbMapper.updateById(employer);

        return companyMapper.toResponse(company);
    }

    private String clean(String value) {
        return value != null && !value.isBlank() ? value.trim() : null;
    }

    private String generateTaxCode(UUID id) {
        return "ITJ-" + id.toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private String buildSlug(String name, UUID id) {
        String base = name == null ? "company" : name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (base.isBlank()) base = "company";
        return base + "-" + id.toString().replace("-", "").substring(0, 8);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
    public CompanyResponse updateByUser(UUID userId, CompanyUpdateRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        log.info("Updating company by employer userId={}", userId);

        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null) {
            throw new AppException(ErrorCode.EMPLOYER_NOT_FOUND);
        }

        if (employer.getCompanyId() == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        Company company = companyDbMapper.selectById(employer.getCompanyId());
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            boolean nameExists = companyDbMapper.exists(
                    new LambdaQueryWrapper<Company>()
                            .eq(Company::getName, request.getName().trim())
                            .ne(Company::getId, company.getId())
            );
            if (nameExists) {
                throw new AppException(ErrorCode.COMPANY_NAME_EXISTED);
            }
        }

        companyMapper.update(company, request);
        company.setUpdatedAt(Instant.now());
        companyDbMapper.updateById(company);

        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYER')")
    public CompanyResponse getByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null || employer.getCompanyId() == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        Company company = companyDbMapper.selectById(employer.getCompanyId());
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getById(UUID companyId) {
        Objects.requireNonNull(companyId, "companyId must not be null");
        Company company = companyDbMapper.selectById(companyId);
        if (company == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }
        return companyMapper.toResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getAll() {
        List<Company> list = companyDbMapper.selectList(
                new LambdaQueryWrapper<Company>().orderByDesc(Company::getCreatedAt)
        );
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream()
                .map(companyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
    public void deleteByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        log.info("Deleting company by employer userId={}", userId);

        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null || employer.getCompanyId() == null) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }
        if (!employer.isAdmin()) {
            throw new AppException(ErrorCode.FORBIDDEN, "Chỉ admin mới có quyền xóa công ty");
        }

        companyDbMapper.deleteById(employer.getCompanyId());
        employer.setCompanyId(null);
        employer.setAdmin(false);
        employerDbMapper.updateById(employer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyBasicResponse> getBasicBatch(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Company> companies = companyDbMapper.selectBatchIds(ids);
        if (companies == null || companies.isEmpty()) {
            return Collections.emptyList();
        }

        return companies.stream().map(c -> CompanyBasicResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .build()
        ).collect(Collectors.toList());
    }
}
