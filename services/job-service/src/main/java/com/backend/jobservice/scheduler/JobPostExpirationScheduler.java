package com.backend.jobservice.scheduler;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.backend.jobservice.entity.JobPost;
import com.backend.jobservice.enums.PostStatus;
import com.backend.jobservice.mapper.db.JobPostDbMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobPostExpirationScheduler {

    private final JobPostDbMapper jobPostDbMapper;

    @Scheduled(cron = "0 */5 * * * *") // mỗi 5 phút
    @Transactional
    public void expirePosts() {
        Instant now = Instant.now();
        int affected = jobPostDbMapper.update(
                null,
                new LambdaUpdateWrapper<JobPost>()
                        .set(JobPost::getStatus, PostStatus.EXPIRED)
                        .set(JobPost::getUpdatedAt, now)
                        .eq(JobPost::getStatus, PostStatus.ACTIVE)
                        .isNotNull(JobPost::getExpiredAt)
                        .le(JobPost::getExpiredAt, now)
        );

        if (affected > 0) {
            log.info("Expired {} job posts at {}", affected, now);
        }
    }
}
