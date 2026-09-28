package com.backend.jobservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.backend.jobservice.core.enums.FilterOpEnum;
import com.backend.jobservice.core.enums.SortDirectionEnum;
import com.backend.jobservice.core.utils.PropertyColumnUtil;
import com.backend.jobservice.core.utils.QueryFilterUtils;
import com.backend.jobservice.dto.request.JobPostFieldFilter;
import com.backend.jobservice.dto.request.JobPostPageRequest;
import com.backend.jobservice.dto.request.GroupMappedJobPostFieldFilter;
import com.backend.jobservice.dto.request.MappedJobPostFieldFilter;
import com.backend.jobservice.dto.request.MappedJobPostQuery;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backend.jobservice.client.AiNlpClient;
import com.backend.jobservice.client.ProfileClient;
import com.backend.jobservice.service.SkillService;
import com.backend.jobservice.dto.JobNormUpdatedEvent;
import com.backend.jobservice.dto.request.JobPostRequest;
import com.backend.jobservice.dto.request.JobPostUpdateRequest;
import com.backend.jobservice.dto.request.JobSkillRequest;
import com.backend.jobservice.dto.request.ProcessPostRequest;
import com.backend.jobservice.dto.response.*;
import com.backend.jobservice.entity.JobPost;
import com.backend.jobservice.entity.JobPostNorm;
import com.backend.jobservice.entity.JobSkill;
import com.backend.jobservice.entity.Skill;
import com.backend.jobservice.enums.ErrorCode;
import com.backend.jobservice.enums.PostStatus;
import com.backend.jobservice.enums.WorkMode;
import com.backend.jobservice.exception.AppException;
import com.backend.jobservice.mapper.db.JobPostDbMapper;
import com.backend.jobservice.mapper.db.JobPostNormDbMapper;
import com.backend.jobservice.mapper.db.JobSkillDbMapper;
import com.backend.jobservice.service.JobNormEventPublisher;
import com.backend.jobservice.service.JobPostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostServiceImpl extends ServiceImpl<JobPostDbMapper, JobPost> implements JobPostService {

    private final JobPostDbMapper jobPostDbMapper;
    private final JobPostNormDbMapper jobPostNormDbMapper;
    private final JobSkillDbMapper jobSkillDbMapper;
    private final ProfileClient profileClient;
    private final SkillService skillService;
    private final AiNlpClient aiNlpClient;
    private final ObjectMapper objectMapper;
    private final JobNormEventPublisher jobNormEventPublisher;

    private void populateSkills(JobPost post) {
        if (post == null || post.getId() == null) return;
        List<JobSkill> skills = jobSkillDbMapper.selectList(
                new LambdaQueryWrapper<JobSkill>().eq(JobSkill::getJobId, post.getId())
        );
        post.setJobSkills(skills != null ? skills : Collections.emptyList());
    }

    private JobPostResponse toJobPostResponse(JobPost post) {
        if (post == null) return null;
        JobPostResponse response = new JobPostResponse();
        BeanUtils.copyProperties(post, response);
        if (post.getJobSkills() != null) {
            List<JobSkillResponse> skillResponses = post.getJobSkills().stream().map(js -> {
                JobSkillResponse jsRes = new JobSkillResponse();
                BeanUtils.copyProperties(js, jsRes);
                return jsRes;
            }).collect(Collectors.toList());
            response.setSkills(skillResponses);
        } else {
            response.setSkills(Collections.emptyList());
        }
        return response;
    }

    private JobPostSummaryResponse toJobPostSummaryResponse(JobPost post) {
        if (post == null) return null;
        JobPostSummaryResponse response = new JobPostSummaryResponse();
        BeanUtils.copyProperties(post, response);
        return response;
    }

    private List<JobPostSummaryResponse> toJobPostSummaryResponseList(List<JobPost> posts) {
        if (posts == null || posts.isEmpty()) return Collections.emptyList();
        return posts.stream().map(this::toJobPostSummaryResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostResponse getPostDetailForAdmin(UUID id) {
        Objects.requireNonNull(id, "Post ID must not be null");
        JobPost post = jobPostDbMapper.selectById(id);
        if (post == null) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        populateSkills(post);
        JobPostResponse response = toJobPostResponse(post);
        fillSkillNames(response.getSkills());

        return response;
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER')")
    @Transactional(rollbackFor = Exception.class)
    public JobPostResponse createPost(UUID employerUserId, JobPostRequest request) {
        Objects.requireNonNull(employerUserId, "Employer User ID must not be null");
        Objects.requireNonNull(request, "Request must not be null");
        log.info("Creating job post by employer={}", employerUserId);

        JobPost post = new JobPost();
        BeanUtils.copyProperties(request, post);
        post.setId(null);
        post.setPostedBy(employerUserId);
        post.setStatus(PostStatus.PENDING);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        // Gọi profile-service lấy companyId của employer
        UUID companyId = resolveCompanyIdOrThrow(employerUserId);
        post.setCompanyId(companyId);

        jobPostDbMapper.insert(post);

        // Validate and save job skills
        List<JobSkill> jobSkills = new ArrayList<>();

        if (request.getSkills() != null) {
            for (JobSkillRequest skillRequest : request.getSkills()) {
                if (skillRequest.getSkillId() == null) {
                    throw new AppException(ErrorCode.SKILL_ID_REQUIRED);
                }

                UUID skillId;
                try {
                    skillId = UUID.fromString(skillRequest.getSkillId());
                } catch (Exception e) {
                    throw new AppException(ErrorCode.INVALID_UUID);
                }

                Skill skill = skillService.getById(skillId);
                if (skill == null) {
                    throw new AppException(ErrorCode.SKILL_NOT_FOUND);
                }

                JobSkill jobSkill = new JobSkill();
                jobSkill.setSkillId(skillId);
                jobSkill.setJobId(post.getId());
                jobSkill.setImportanceLevel(skillRequest.getImportanceLevel());
                jobSkill.setNote(skillRequest.getNote());

                jobSkillDbMapper.insert(jobSkill);
                jobSkills.add(jobSkill);
            }
        }

        post.setJobSkills(jobSkills);

        try {
            ProcessPostRequest nlpReq = buildProcessPostRequest(post, request);
            ProcessPostResponse nlpRes = aiNlpClient.processJob(nlpReq);
            applyNlpResultToPost(post, nlpRes);

            if (post.getJobPostNorm() != null) {
                jobPostNormDbMapper.insert(post.getJobPostNorm());
            }

            jobPostDbMapper.updateById(post);

            jobNormEventPublisher.publish(
                    JobNormUpdatedEvent.builder()
                            .internshipPostId(post.getId())
                            .companyId(companyId)
                            .skillsNorm(
                                    post.getJobPostNorm() != null && post.getJobPostNorm().getSkillsNorm() != null
                                            ? post.getJobPostNorm().getSkillsNorm()
                                            : Collections.emptyList()
                            )
                            .workMode(post.getWorkMode() != null
                                    ? post.getWorkMode().name()
                                    : null)
                            .locationLat(post.getJobPostNorm() != null ? post.getJobPostNorm().getLat() : null)
                            .locationLon(post.getJobPostNorm() != null ? post.getJobPostNorm().getLon() : null)
                            .updatedAt(OffsetDateTime.now())
                            .build()
            );

        } catch (Exception ex) {
            log.error("NLP processing failed for job post id={}: {}", post.getId(), ex.getMessage());
            post.setNlpStatus("ERROR");
            post.setNlpError(ex.getMessage());
            jobPostDbMapper.updateById(post);
        }

        JobPostResponse response = toJobPostResponse(post);
        fillSkillNames(response.getSkills());
        return response;
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER')")
    @Transactional(rollbackFor = Exception.class)
    public JobPostResponse updatePost(UUID employerUserId, UUID postId, JobPostUpdateRequest request) {
        Objects.requireNonNull(employerUserId, "Employer User ID must not be null");
        Objects.requireNonNull(postId, "Post ID must not be null");
        Objects.requireNonNull(request, "Request must not be null");
        log.info("Updating job post id={} by employer={}", postId, employerUserId);

        JobPost post = jobPostDbMapper.selectOne(
                new LambdaQueryWrapper<JobPost>()
                        .eq(JobPost::getId, postId)
                        .eq(JobPost::getPostedBy, employerUserId)
        );

        if (post == null) {
            throw new AppException(ErrorCode.POST_NOT_FOUND_OR_FORBIDDEN);
        }

        if (post.getStatus().equals(PostStatus.EXPIRED)) {
            throw new AppException(ErrorCode.POST_EXPIRED);
        }

        BeanUtils.copyProperties(request, post);
        post.setUpdatedAt(Instant.now());

        if (post.getStatus().equals(PostStatus.ACTIVE)) {
            post.setStatus(PostStatus.PENDING);
        }

        List<JobSkill> newSkills = new ArrayList<>();

        if (request.getSkills() != null) {
            // Xóa trong DB
            jobSkillDbMapper.delete(
                    new LambdaQueryWrapper<JobSkill>().eq(JobSkill::getJobId, post.getId())
            );

            for (JobSkillRequest skillRequest : request.getSkills()) {
                if (skillRequest.getSkillId() == null) {
                    throw new AppException(ErrorCode.SKILL_ID_REQUIRED);
                }

                UUID skillId;
                try {
                    skillId = UUID.fromString(skillRequest.getSkillId());
                } catch (Exception e) {
                    throw new AppException(ErrorCode.INVALID_UUID);
                }

                Skill skill = skillService.getById(skillId);
                if (skill == null) {
                    throw new AppException(ErrorCode.SKILL_NOT_FOUND);
                }

                JobSkill jobSkill = new JobSkill();
                jobSkill.setSkillId(skillId);
                jobSkill.setJobId(post.getId());
                jobSkill.setImportanceLevel(skillRequest.getImportanceLevel());
                jobSkill.setNote(skillRequest.getNote());

                jobSkillDbMapper.insert(jobSkill);
                newSkills.add(jobSkill);
            }
            post.setJobSkills(newSkills);
        }

        jobPostDbMapper.updateById(post);

        JobPostResponse response = toJobPostResponse(post);
        fillSkillNames(response.getSkills());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostResponse getEmployerPostDetail(UUID employerUserId, UUID postId) {
        Objects.requireNonNull(employerUserId, "Employer User ID must not be null");
        Objects.requireNonNull(postId, "Post ID must not be null");

        JobPost post = jobPostDbMapper.selectById(postId);
        if (post == null) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        if (!post.getPostedBy().equals(employerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền xem bài đăng này");
        }
        populateSkills(post);
        return toJobPostResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostResponse getPostDetail(UUID postId) {
        Objects.requireNonNull(postId, "Post ID must not be null");
        JobPost post = jobPostDbMapper.selectOne(
                new LambdaQueryWrapper<JobPost>()
                        .eq(JobPost::getId, postId)
                        .eq(JobPost::getStatus, PostStatus.ACTIVE)
        );

        if (post == null) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        if (post.getExpiredAt() == null || !post.getExpiredAt().isAfter(Instant.now())) {
            throw new AppException(ErrorCode.POST_EXPIRED);
        }

        populateSkills(post);
        JobPostResponse response = toJobPostResponse(post);
        fillSkillNames(response.getSkills());

        return response;
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('SYSTEM_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public void hidePost(UUID employerUserId, UUID postId) {
        Objects.requireNonNull(postId, "Post ID must not be null");
        log.info("Toggling hide post id={} by user={}", postId, employerUserId);

        JobPost post = jobPostDbMapper.selectById(postId);
        if (post == null) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        if (post.getStatus() == PostStatus.ACTIVE) {
            post.setStatus(PostStatus.HIDDEN);
        } else if (post.getStatus() == PostStatus.HIDDEN) {
            post.setStatus(PostStatus.PENDING);
        } else if (post.getStatus() == PostStatus.PENDING) {
            post.setStatus(PostStatus.HIDDEN);
        }
        jobPostDbMapper.updateById(post);
    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public JobPostResponse approvePost(UUID postId, UUID adminId) {
        Objects.requireNonNull(postId, "Post ID must not be null");
        log.info("Approving post id={} by admin={}", postId, adminId);

        JobPost post = jobPostDbMapper.selectById(postId);
        if (post == null) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        if (post.getStatus() != PostStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_POST_STATUS);
        }

        Instant now = Instant.now();

        post.setStatus(PostStatus.ACTIVE);
        post.setUpdatedAt(now);

        if (post.getExpiredAt() == null) {
            post.setExpiredAt(now.plus(Duration.ofDays(30)));
        }

        jobPostDbMapper.updateById(post);
        populateSkills(post);
        return toJobPostResponse(post);
    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    @Transactional(rollbackFor = Exception.class)
    public void rejectPost(UUID postId) {
        Objects.requireNonNull(postId, "Post ID must not be null");
        log.info("Rejecting post id={}", postId);

        JobPost post = jobPostDbMapper.selectById(postId);
        if (post == null) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }

        if (post.getStatus() != PostStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_POST_STATUS);
        }

        post.setStatus(PostStatus.REJECTED);
        post.setUpdatedAt(Instant.now());
        jobPostDbMapper.updateById(post);
    }

    @Override
    @PreAuthorize("hasRole('SYSTEM_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<JobPostSummaryResponse> getPendingPosts() {
        List<JobPost> pendingPosts = jobPostDbMapper.selectList(
                new LambdaQueryWrapper<JobPost>()
                        .eq(JobPost::getStatus, PostStatus.PENDING)
                        .orderByDesc(JobPost::getCreatedAt)
        );
        return toJobPostSummaryResponseList(pendingPosts);
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER') or hasRole('SYSTEM_ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<JobPostSummaryResponse> getRejectedAndHiddenPosts() {
        List<JobPost> hidden = jobPostDbMapper.selectList(
                new LambdaQueryWrapper<JobPost>()
                        .eq(JobPost::getStatus, PostStatus.HIDDEN)
                        .orderByDesc(JobPost::getCreatedAt)
        );
        List<JobPost> rejected = jobPostDbMapper.selectList(
                new LambdaQueryWrapper<JobPost>()
                        .eq(JobPost::getStatus, PostStatus.REJECTED)
                        .orderByDesc(JobPost::getCreatedAt)
        );

        List<JobPost> combined = Stream.concat(
                (hidden != null ? hidden.stream() : Stream.empty()),
                (rejected != null ? rejected.stream() : Stream.empty())
        ).sorted(Comparator.comparing(JobPost::getCreatedAt).reversed())
         .collect(Collectors.toList());

        return toJobPostSummaryResponseList(combined);
    }

    @Override
    @Transactional(readOnly = true)
    public ListDataRes<JobPostSummaryResponse> getJobPostPage(JobPostPageRequest request) {
        if (request == null) {
            request = new JobPostPageRequest();
        }

        MappedJobPostQuery query = buildMappedJobPostQuery(request);

        Page<JobPost> mpPage = new Page<>(query.getPageIndex(), query.getPageSize());

        List<JobPost> posts = jobPostDbMapper.getJobPostPage(mpPage, query);

        List<JobPostSummaryResponse> summaries = posts.stream()
                .map(this::toJobPostSummaryResponse)
                .collect(Collectors.toList());

        fillCompanyNames(posts, summaries);

        return new ListDataRes<>(summaries, mpPage);
    }

    private MappedJobPostQuery buildMappedJobPostQuery(JobPostPageRequest request) {
        boolean[] hasExplicitStatusHolder = new boolean[]{false};
        List<GroupMappedJobPostFieldFilter> mappedGroups = mapGroupFilters(request.getPostFieldFilter(), hasExplicitStatusHolder);

        MappedJobPostQuery query = new MappedJobPostQuery();
        query.setPageIndex(request.getPageIndex());
        query.setPageSize(request.getPageSize());
        query.setKeyword(request.getKeyword() != null ? request.getKeyword().trim() : null);
        query.setSkillId(request.getSkillId());
        query.setCompanyId(request.getCompanyId());
        query.setPostIds(request.getPostIds());
        query.setFilters(mappedGroups);
        query.setHaveFilter(!mappedGroups.isEmpty());
        query.setHasExplicitStatus(hasExplicitStatusHolder[0]);

        if (request.getSortBy() != null && !request.getSortBy().isBlank()) {
            String sortCol = PropertyColumnUtil.getColumn(JobPost.class, request.getSortBy().trim());
            if (sortCol == null && request.getSortBy().trim().matches("^[a-zA-Z0-9_]+$")) {
                sortCol = request.getSortBy().trim();
            }
            if (sortCol != null) {
                query.setSortBy("t0." + sortCol);
                query.setSortDirection(request.getSortDirection() != null ? request.getSortDirection().getDisplayName() : "DESC");
            }
        }

        return query;
    }

    private List<GroupMappedJobPostFieldFilter> mapGroupFilters(List<List<JobPostFieldFilter>> rawGroups, boolean[] hasExplicitStatusHolder) {
        if (rawGroups == null || rawGroups.isEmpty()) {
            return Collections.emptyList();
        }

        List<GroupMappedJobPostFieldFilter> mappedGroups = new ArrayList<>();
        for (List<JobPostFieldFilter> orGroup : rawGroups) {
            if (orGroup == null || orGroup.isEmpty()) continue;

            List<MappedJobPostFieldFilter> andFilters = new ArrayList<>();
            for (JobPostFieldFilter filter : orGroup) {
                MappedJobPostFieldFilter mapped = mapFieldFilter(filter, hasExplicitStatusHolder);
                if (mapped != null) {
                    andFilters.add(mapped);
                }
            }

            if (!andFilters.isEmpty()) {
                GroupMappedJobPostFieldFilter group = new GroupMappedJobPostFieldFilter();
                group.setFilters(andFilters);
                mappedGroups.add(group);
            }
        }
        return mappedGroups;
    }

    private MappedJobPostFieldFilter mapFieldFilter(JobPostFieldFilter filter, boolean[] hasExplicitStatusHolder) {
        if (filter == null || filter.getField() == null || filter.getField().isBlank()
                || filter.getValue() == null || filter.getValue().isBlank()) {
            return null;
        }

        String fieldName = filter.getField().trim();
        if ("status".equalsIgnoreCase(fieldName)) {
            hasExplicitStatusHolder[0] = true;
        }

        String col = PropertyColumnUtil.getColumn(JobPost.class, fieldName);
        if (col == null || col.isBlank()) {
            if (fieldName.matches("^[a-zA-Z0-9_]+$")) {
                col = fieldName;
            } else {
                return null;
            }
        }

        FilterOpEnum op = (filter.getOp() != null) ? filter.getOp() : FilterOpEnum.EQUAL;
        String val = filter.getValue().trim();

        MappedJobPostFieldFilter r = new MappedJobPostFieldFilter();
        r.setAlias("t0.");
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

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<JobPostSummaryResponse> searchPosts(
            String keyword, String workMode,
            UUID skillId, UUID companyId,
            String location, String level, Pageable pageable
    ) {
        JobPostPageRequest request = new JobPostPageRequest();
        request.setPageIndex(pageable.getPageNumber() + 1);
        request.setPageSize(pageable.getPageSize());
        request.setKeyword(keyword);
        request.setSkillId(skillId);
        request.setCompanyId(companyId);

        List<JobPostFieldFilter> andFilters = new ArrayList<>();
        if (workMode != null && !workMode.isBlank() && !"ALL".equalsIgnoreCase(workMode)) {
            andFilters.add(new JobPostFieldFilter("workMode", FilterOpEnum.EQUAL, workMode.toUpperCase()));
        }
        if (location != null && !location.isBlank()) {
            andFilters.add(new JobPostFieldFilter("location", FilterOpEnum.CONTAINS, location.trim()));
        }
        if (level != null && !level.isBlank()) {
            andFilters.add(new JobPostFieldFilter("level", FilterOpEnum.EQUAL, level.trim()));
        }
        if (!andFilters.isEmpty()) {
            request.setPostFieldFilter(List.of(andFilters));
        }

        ListDataRes<JobPostSummaryResponse> res = getJobPostPage(request);
        return new PageImpl<>(res.getList(), pageable, res.getTotal());
    }

    private void fillCompanyNames(List<JobPost> posts, List<JobPostSummaryResponse> summaries) {
        if (posts == null || posts.isEmpty()) return;

        List<UUID> companyIds = posts.stream()
                .map(JobPost::getCompanyId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (companyIds.isEmpty()) return;

        try {
            ApiResponse<List<CompanyBasicResponse>> api = profileClient.getCompaniesBatch(companyIds);
            if (api == null || api.getData() == null) return;

            Map<UUID, String> map = api.getData().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(CompanyBasicResponse::getId, CompanyBasicResponse::getName, (a, b) -> a));

            for (int i = 0; i < posts.size(); i++) {
                UUID cid = posts.get(i).getCompanyId();
                if (cid != null && map.containsKey(cid)) {
                    summaries.get(i).setCompanyName(map.get(cid));
                }
            }
        } catch (Exception ignored) {
            // profile-service down thì để companyName null, không làm fail search
        }
    }

    private void fillSkillNames(List<JobSkillResponse> skills) {
        if (skills == null || skills.isEmpty()) return;
        List<UUID> skillIds = skills.stream()
                .map(JobSkillResponse::getSkillId)
                .distinct()
                .collect(Collectors.toList());

        try {
            List<SkillResponse> skillList = skillService.getSkillsByIds(skillIds);
            if (skillList != null && !skillList.isEmpty()) {
                Map<UUID, String> skillMap = skillList.stream()
                        .collect(Collectors.toMap(SkillResponse::getId, SkillResponse::getName, (a, b) -> a));

                for (JobSkillResponse js : skills) {
                    if (skillMap.containsKey(js.getSkillId())) {
                        js.setSkillName(skillMap.get(js.getSkillId()));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching batch skills: {}", e.getMessage());
        }
    }

    private ProcessPostRequest buildProcessPostRequest(JobPost post,
                                                       JobPostRequest originalReq) {

        return ProcessPostRequest.builder()
                .postId(post.getId().toString())
                .title(post.getTitle())
                .position(post.getPosition())
                .description(post.getDescription())
                .duration(post.getDuration())
                .location(post.getLocation())
                .workMode(
                        post.getWorkMode() != null
                                ? post.getWorkMode().name()
                                : null
                )
                .skills(originalReq.getSkills())
                .build();
    }

    private void applyNlpResultToPost(JobPost post,
                                      ProcessPostResponse res) throws JsonProcessingException {
        if (res == null) return;

        JobPostNorm norm = new JobPostNorm();
        norm.setJobId(post.getId());
        norm.setJobPost(post);

        if (res.getSkillsNorm() != null) {
            norm.setSkillsNorm(res.getSkillsNorm());
        }

        norm.setExperienceYearsMin(res.getExperienceYearsMin());
        norm.setExperienceYearsMax(res.getExperienceYearsMax());
        norm.setExperienceLevel(res.getExperienceLevel());

        norm.setEducationLevels(null);
        norm.setMajors(null);

        if (res.getDomains() != null) {
            norm.setDomains(res.getDomains().toArray(new String[0]));
        }

        if (res.getWorkModesNorm() != null) {
            norm.setWorkModesNorm(res.getWorkModesNorm().toArray(new String[0]));
        }

        if (res.getLocationsNorm() != null) {
            norm.setLocationsNorm(res.getLocationsNorm().toArray(new String[0]));
        }

        norm.setDurationNormMonths(res.getDurationMonthsMin());

        norm.setLat(res.getLat());
        norm.setLon(res.getLon());

        norm.setModelVersion(res.getModelVersion());

        post.setJobPostNorm(norm);

        post.setNlpStatus("DONE");
        post.setNlpError(null);
        post.setProcessedAt(res.getProcessedAt());
    }

    private UUID resolveCompanyIdOrThrow(UUID employerUserId) {
        try {
            ApiResponse<UUID> api = profileClient.getMyCompanyId(
                    employerUserId.toString(),
                    "EMPLOYER"
            );

            if (api == null || api.getData() == null) {
                throw new AppException(ErrorCode.EMPLOYER_HAS_NO_COMPANY);
            }

            return api.getData();

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<JobPostResponse> getMyPosts(UUID employerUserId, int page, int size) {
        Objects.requireNonNull(employerUserId, "Employer User ID must not be null");
        Page<JobPost> mpPage = new Page<>(page + 1, size);

        LambdaQueryWrapper<JobPost> wrapper = new LambdaQueryWrapper<JobPost>()
                .eq(JobPost::getPostedBy, employerUserId)
                .orderByDesc(JobPost::getCreatedAt);

        jobPostDbMapper.selectPage(mpPage, wrapper);

        List<JobPostResponse> list = mpPage.getRecords().stream()
                .map(this::toJobPostResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(list, PageRequest.of(page, size), mpPage.getTotal());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAdminStats() {
        long totalJobs = jobPostDbMapper.selectCount(null);
        long pendingJobs = jobPostDbMapper.selectCount(
                new LambdaQueryWrapper<JobPost>().eq(JobPost::getStatus, PostStatus.PENDING)
        );
        long activeJobs = jobPostDbMapper.selectCount(
                new LambdaQueryWrapper<JobPost>().eq(JobPost::getStatus, PostStatus.ACTIVE)
        );
        long hiddenJobs = jobPostDbMapper.selectCount(
                new LambdaQueryWrapper<JobPost>().eq(JobPost::getStatus, PostStatus.HIDDEN)
        );
        long rejectedJobs = jobPostDbMapper.selectCount(
                new LambdaQueryWrapper<JobPost>().eq(JobPost::getStatus, PostStatus.REJECTED)
        );

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", totalJobs);
        stats.put("pendingJobs", pendingJobs);
        stats.put("activeJobs", activeJobs);
        stats.put("hiddenJobs", hiddenJobs);
        stats.put("rejectedJobs", rejectedJobs);
        return stats;
    }
}
