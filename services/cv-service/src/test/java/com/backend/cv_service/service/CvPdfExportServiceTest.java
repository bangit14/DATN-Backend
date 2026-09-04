package com.backend.cv_service.service;

import com.backend.cv_service.util.CvPdfTemplateBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CvPdfExportServiceTest {

    @Test
    public void testExportSamplePdf() {
        CvPdfExportService service = new CvPdfExportService();
        String html = CvPdfTemplateBuilder.buildSampleCvHtml(
                "Nguyễn Duy Đăng Bảng",
                "Senior Java Backend Developer",
                "bangnd@ptit.edu.vn",
                "0987654321",
                "Hà Nội, Việt Nam",
                "Lập trình viên Backend với hơn 5 năm kinh nghiệm phát triển hệ thống microservices sử dụng Java, Spring Boot, MyBatis-Plus, Docker và PostgreSQL.",
                List.of("Java", "Spring Boot", "MyBatis-Plus", "PostgreSQL", "Docker", "Redis", "Kafka"),
                List.of("Phát triển hệ thống Cổng thông tin Việc làm IT (Job Portal PTIT).")
        );

        byte[] pdfBytes = service.exportToPdf(html);
        Assertions.assertNotNull(pdfBytes);
        Assertions.assertTrue(pdfBytes.length > 0, "PDF bytes should not be empty");
        // PDF header magic bytes: %PDF-
        Assertions.assertEquals('%', (char) pdfBytes[0]);
        Assertions.assertEquals('P', (char) pdfBytes[1]);
        Assertions.assertEquals('D', (char) pdfBytes[2]);
        Assertions.assertEquals('F', (char) pdfBytes[3]);
    }
}
