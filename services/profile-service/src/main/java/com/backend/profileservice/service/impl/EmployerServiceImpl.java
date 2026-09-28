package com.backend.profileservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backend.profileservice.core.base.BaseService;
import com.backend.profileservice.dto.request.AutoCreateProfileRequest;
import com.backend.profileservice.dto.request.CompanyCreateRequest;
import com.backend.profileservice.dto.request.EmployerUpdateRequest;
import com.backend.profileservice.dto.response.CompanyResponse;
import com.backend.profileservice.dto.response.EmployerResponse;
import com.backend.profileservice.entity.Company;
import com.backend.profileservice.entity.Employer;
import com.backend.profileservice.enums.ErrorCode;
import com.backend.profileservice.enums.Gender;
import com.backend.profileservice.exception.AppException;
import com.backend.profileservice.mapper.EmployerMapper;
import com.backend.profileservice.mapper.db.CompanyDbMapper;
import com.backend.profileservice.mapper.db.EmployerDbMapper;
import com.backend.profileservice.service.CompanyService;
import com.backend.profileservice.service.EmployerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployerServiceImpl extends BaseService<EmployerDbMapper, Employer> implements EmployerService {

    private final EmployerDbMapper employerDbMapper;
    private final CompanyDbMapper companyDbMapper;
    private final EmployerMapper employerMapper;
    private final CompanyService companyService;

    private void populateCompany(Employer employer) {
        if (employer != null && employer.getCompanyId() != null) {
            employer.setCompany(companyDbMapper.selectById(employer.getCompanyId()));
        }
    }

    private void populateCompanies(List<Employer> employers) {
        if (employers == null || employers.isEmpty()) return;
        List<UUID> companyIds = employers.stream()
                .map(Employer::getCompanyId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (companyIds.isEmpty()) return;

        List<Company> companies = companyDbMapper.selectBatchIds(companyIds);
        if (companies == null) return;
        Map<UUID, Company> companyMap = companies.stream()
                .collect(Collectors.toMap(Company::getId, c -> c, (a, b) -> a));

        employers.forEach(e -> {
            if (e.getCompanyId() != null) {
                e.setCompany(companyMap.get(e.getCompanyId()));
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse getByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null) {
            throw new AppException(ErrorCode.EMPLOYER_NOT_FOUND);
        }

        populateCompany(employer);
        return employerMapper.toResponse(employer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse updateMyProfile(UUID userId, EmployerUpdateRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        log.info("Updating employer profile for userId={}", userId);

        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null) {
            throw new AppException(ErrorCode.EMPLOYER_NOT_FOUND);
        }

        employerMapper.updateProfile(employer, request);
        employerDbMapper.updateById(employer);

        populateCompany(employer);
        return employerMapper.toResponse(employer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse joinCompany(UUID userId, UUID companyId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(companyId, "companyId must not be null");
        log.info("Employer userId={} joining companyId={}", userId, companyId);

        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null) {
            throw new AppException(ErrorCode.EMPLOYER_NOT_FOUND);
        }

        boolean companyExists = companyDbMapper.exists(
                new LambdaQueryWrapper<Company>().eq(Company::getId, companyId)
        );
        if (!companyExists) {
            throw new AppException(ErrorCode.COMPANY_NOT_FOUND);
        }

        employer.setCompanyId(companyId);
        employer.setAdmin(false);
        employer.setUpdatedAt(Instant.now());
        employerDbMapper.updateById(employer);

        populateCompany(employer);
        return employerMapper.toResponse(employer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
    public EmployerResponse createCompanyAndJoin(UUID userId, CompanyCreateRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(request, "request must not be null");
        log.info("Employer userId={} creating company name={}", userId, request.getName());

        CompanyResponse companyRes = companyService.create(userId, request);
        return getByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('EMPLOYER')")
    public void leaveCompany(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        log.info("Employer userId={} leaving company", userId);

        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null || employer.getCompanyId() == null) {
            throw new AppException(ErrorCode.EMPLOYER_NOT_FOUND);
        }

        if (employer.isAdmin()) {
            Long adminCount = employerDbMapper.selectCount(
                    new LambdaQueryWrapper<Employer>()
                            .eq(Employer::getCompanyId, employer.getCompanyId())
                            .eq(Employer::isAdmin, true)
            );
            if (adminCount != null && adminCount <= 1) {
                throw new AppException(ErrorCode.FORBIDDEN, "Bạn là admin duy nhất, vui lòng chỉ định admin khác trước khi rời công ty");
            }
        }

        employer.setCompanyId(null);
        employer.setAdmin(false);
        employer.setUpdatedAt(Instant.now());
        employerDbMapper.updateById(employer);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('EMPLOYER')")
    public List<EmployerResponse> getAllByCompany(UUID callerUserId, UUID companyId) {
        Objects.requireNonNull(companyId, "companyId must not be null");

        List<Employer> members = employerDbMapper.selectList(
                new LambdaQueryWrapper<Employer>().eq(Employer::getCompanyId, companyId)
        );
        if (members == null || members.isEmpty()) {
            return Collections.emptyList();
        }

        populateCompanies(members);
        return members.stream()
                .map(employerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployerResponse getPublicProfile(UUID viewerUserId, UUID targetUserId) {
        Objects.requireNonNull(targetUserId, "targetUserId must not be null");
        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, targetUserId)
        );
        if (employer == null) {
            throw new AppException(ErrorCode.EMPLOYER_NOT_FOUND);
        }

        populateCompany(employer);
        return employerMapper.toResponse(employer);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFullNameByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        return employer != null ? employer.getName() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getMyCompanyId(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");
        Employer employer = employerDbMapper.selectOne(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (employer == null || employer.getCompanyId() == null) {
            return null;
        }
        return employer.getCompanyId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoCreateProfile(AutoCreateProfileRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        autoCreateProfile(
                request.getUserId(),
                request.getFullName(),
                request.getPhone(),
                request.getAvatarUrl(),
                request.getPosition(),
                request
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoCreateProfile(UUID userId, String fullName) {
        autoCreateProfile(userId, fullName, null, null, null, null);
    }

    private void autoCreateProfile(UUID userId, String fullName, String phone, String avatarUrl, String position, AutoCreateProfileRequest request) {
        Objects.requireNonNull(userId, "userId must not be null");
        boolean exists = employerDbMapper.exists(
                new LambdaQueryWrapper<Employer>().eq(Employer::getUserId, userId)
        );
        if (exists) {
            log.warn("Employer profile already exists for userId={}", userId);
            return;
        }

        Employer profile = new Employer();
        profile.setId(UUID.randomUUID());
        profile.setUserId(userId);
        profile.setName(fullName != null && !fullName.isBlank() ? fullName.trim() : "Nhà tuyển dụng");
        profile.setGender(Gender.UNKNOWN);
        profile.setAdmin(false);
        profile.setPhone(phone != null && !phone.isBlank() ? phone.trim() : null);
        profile.setAvatarUrl(avatarUrl != null && !avatarUrl.isBlank() ? avatarUrl.trim() : null);
        profile.setPosition(position != null && !position.isBlank() ? position.trim() : null);
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());

        employerDbMapper.insert(profile);

        if (request != null && request.getCompanyName() != null && !request.getCompanyName().isBlank()) {
            String companyName = request.getCompanyName().trim();
            boolean companyExists = companyDbMapper.exists(
                    new LambdaQueryWrapper<Company>().eq(Company::getName, companyName)
            );
            if (companyExists) {
                throw new AppException(ErrorCode.COMPANY_NAME_EXISTED);
            }

            Company company = new Company();
            company.setId(UUID.randomUUID());
            company.setName(companyName);
            company.setIndustry(clean(request.getCompanyIndustry()));
            company.setDescription(clean(request.getCompanyDescription()));
            company.setLogoUrl(clean(request.getCompanyLogoUrl()));
            company.setWebsiteUrl(clean(request.getCompanyWebsiteUrl()));
            company.setAddress(clean(request.getCompanyAddress()));
            company.setCompanySize(clean(request.getCompanySize()));
            company.setVerificationStatus("PENDING");
            company.setCreatedAt(Instant.now());
            company.setUpdatedAt(Instant.now());
            companyDbMapper.insert(company);

            profile.setCompanyId(company.getId());
            profile.setAdmin(true);
            profile.setUpdatedAt(Instant.now());
            employerDbMapper.updateById(profile);
            log.info("Auto-created company id={} and linked it to employer userId={}", company.getId(), userId);
        }

        log.info("Auto-created employer profile for userId={}", userId);
    }

    private String clean(String value) {
        return value != null && !value.isBlank() ? value.trim() : null;
    }
}
