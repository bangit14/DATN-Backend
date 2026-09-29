package com.backend.candidateservice;

import com.backend.candidateservice.cv.config.InternalHeaderAuthFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.backend.candidateservice.profile",
                "com.backend.candidateservice.cv",
                "com.backend.candidateservice.matching"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.backend.candidateservice.cv.config.SecurityConfig.class,
                        com.backend.candidateservice.cv.config.MybatisPlusConfig.class,
                        InternalHeaderAuthFilter.class,
                        com.backend.candidateservice.matching.config.SecurityConfig.class,
                        com.backend.candidateservice.matching.config.MybatisPlusConfig.class,
                        com.backend.candidateservice.matching.config.InternalHeaderAuthFilter.class
                }
        )
)
@MapperScan({
        "com.backend.candidateservice.profile.mapper.db",
        "com.backend.candidateservice.cv.mapper.db",
        "com.backend.candidateservice.matching.mapper.db"
})
@EnableFeignClients(basePackages = {
        "com.backend.candidateservice.profile.client",
        "com.backend.candidateservice.cv.client"
})
public class CandidateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CandidateServiceApplication.class, args);
    }
}
