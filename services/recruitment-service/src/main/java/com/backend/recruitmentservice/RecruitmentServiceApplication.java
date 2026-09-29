package com.backend.recruitmentservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.backend.recruitmentservice.job", "com.backend.recruitmentservice.application"},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.backend.recruitmentservice.application.config.SecurityConfig.class,
                        com.backend.recruitmentservice.application.config.MybatisPlusConfig.class,
                        com.backend.recruitmentservice.application.security.JwtHeaderAuthenticationFilter.class
                }
        )
)
@MapperScan({"com.backend.recruitmentservice.job.mapper.db", "com.backend.recruitmentservice.application.mapper.db"})
@EnableFeignClients(basePackages = {"com.backend.recruitmentservice.job.client", "com.backend.recruitmentservice.application.client"})
@EnableScheduling
public class RecruitmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitmentServiceApplication.class, args);
    }
}
