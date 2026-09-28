package com.backend.cv_service.controller;

import com.backend.cv_service.core.enums.SortDirectionEnum;
import com.backend.cv_service.dto.CvDetailDto;
import com.backend.cv_service.dto.CvPageRequest;
import com.backend.cv_service.dto.CvSummaryDto;
import com.backend.cv_service.dto.EmployerCvResponse;
import com.backend.cv_service.dto.UpdateCvNameRequest;
import com.backend.cv_service.dto.response.ListDataRes;
import com.backend.cv_service.service.CvPdfExportService;
import com.backend.cv_service.service.CvService;
import com.backend.cv_service.util.CvPdfTemplateBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cv/v1")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;
    private final CvPdfExportService cvPdfExportService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'CANDIDATE', 'ROLE_STUDENT', 'ROLE_CANDIDATE')")
    public ResponseEntity<CvSummaryDto> uploadNewCv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("cvName") String cvName,
            @AuthenticationPrincipal UUID studentId
    ) {
        CvSummaryDto newCv = cvService.uploadExtractAndSaveCv(studentId, cvName, file);
        return new ResponseEntity<>(newCv, HttpStatus.CREATED);
    }

    @GetMapping("/my-cvs")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'CANDIDATE', 'ROLE_STUDENT', 'ROLE_CANDIDATE')")
    public ResponseEntity<List<CvSummaryDto>> getMyCVs(
            @AuthenticationPrincipal UUID studentId
    ) {
        List<CvSummaryDto> cvs = cvService.findAllByStudentId(studentId);
        return ResponseEntity.ok(cvs);
    }

    @GetMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'CANDIDATE', 'ROLE_STUDENT', 'ROLE_CANDIDATE')")
    public ResponseEntity<CvDetailDto> getCvById(
            @PathVariable("cvId") Long cvId,
            @AuthenticationPrincipal UUID studentId
    ) {
        CvDetailDto cvDetail = cvService.findCvDetailById(cvId, studentId);
        return ResponseEntity.ok(cvDetail);
    }

    @PutMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'CANDIDATE', 'ROLE_STUDENT', 'ROLE_CANDIDATE')")
    public ResponseEntity<CvSummaryDto> updateCvName(
            @PathVariable("cvId") Long cvId,
            @RequestBody UpdateCvNameRequest request,
            @AuthenticationPrincipal UUID studentId
    ) {
        CvSummaryDto updatedCv = cvService.updateCvName(cvId, studentId, request.getNewCvName());
        return ResponseEntity.ok(updatedCv);
    }

    @DeleteMapping("/{cvId}")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'CANDIDATE', 'ROLE_STUDENT', 'ROLE_CANDIDATE')")
    public ResponseEntity<Void> deleteCv(
            @PathVariable("cvId") Long cvId,
            @AuthenticationPrincipal UUID studentId
    ) {
        cvService.deleteCv(cvId, studentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cvId}/set-default")
    @PreAuthorize("hasAnyAuthority('STUDENT', 'CANDIDATE', 'ROLE_STUDENT', 'ROLE_CANDIDATE')")
    public ResponseEntity<String> setDefaultCv(
            @PathVariable("cvId") Long cvId,
            @AuthenticationPrincipal UUID studentId
    ) {
        cvService.setDefaultCv(cvId, studentId);
        return ResponseEntity.ok("CV với id " + cvId + " đã được đặt làm mặc định.");
    }

    @PostMapping("/export-pdf")
    public ResponseEntity<byte[]> exportHtmlToPdf(@RequestBody String htmlContent) {
        byte[] pdfBytes = cvPdfExportService.exportToPdf(htmlContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cv.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/sample-pdf")
    public ResponseEntity<byte[]> exportSamplePdf() {
        String html = CvPdfTemplateBuilder.buildSampleCvHtml(
                "Nguyễn Duy Đăng Bảng",
                "Senior Java Backend Developer",
                "bangnd@ptit.edu.vn",
                "0987654321",
                "Hà Nội, Việt Nam",
                "Lập trình viên Backend với hơn 5 năm kinh nghiệm phát triển hệ thống microservices sử dụng Java, Spring Boot, MyBatis-Plus, Docker và PostgreSQL.",
                List.of("Java", "Spring Boot", "MyBatis-Plus", "PostgreSQL", "Docker", "Redis", "Kafka", "Microservices", "REST API"),
                List.of(
                        "Phát triển hệ thống Cổng thông tin Việc làm IT (Job Portal PTIT) kiến trúc 9 Microservices.",
                        "Tối ưu hóa hiệu năng truy vấn cơ sở dữ liệu và tích hợp OpenHTMLtoPDF xuất CV tự động."
                )
        );
        byte[] pdfBytes = cvPdfExportService.exportToPdf(html);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cv_sample.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping({"/employer/filter", "/employer/search", "/employer/getCvPage"})
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER', 'ADMIN', 'ROLE_ADMIN', 'RECRUITER')")
    public ResponseEntity<ListDataRes<EmployerCvResponse>> filterCvsForEmployer(
            @RequestBody(required = false) CvPageRequest request
    ) {
        if (request == null) {
            request = new CvPageRequest();
        }
        ListDataRes<EmployerCvResponse> response = cvService.filterCvsForEmployer(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employer/search")
    @PreAuthorize("hasAnyAuthority('EMPLOYER', 'ROLE_EMPLOYER', 'ADMIN', 'ROLE_ADMIN', 'RECRUITER')")
    public ResponseEntity<ListDataRes<EmployerCvResponse>> searchCvsForEmployer(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "educationLevel", required = false) String educationLevel,
            @RequestParam(value = "yearsTotalMin", required = false) Double yearsTotalMin,
            @RequestParam(value = "yearsTotalMax", required = false) Double yearsTotalMax,
            @RequestParam(value = "isDefault", required = false) Boolean isDefault,
            @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortDirection", required = false) String sortDirection
    ) {
        CvPageRequest request = new CvPageRequest();
        request.setPageIndex(pageIndex);
        request.setPageSize(pageSize);
        request.setKeyword(keyword);
        request.setEducationLevel(educationLevel);
        request.setYearsTotalMin(yearsTotalMin);
        request.setYearsTotalMax(yearsTotalMax);
        request.setIsDefault(isDefault);
        request.setSortBy(sortBy);
        if (sortDirection != null && !sortDirection.isBlank()) {
            request.setSortDirection(SortDirectionEnum.fromValue(sortDirection));
        }
        ListDataRes<EmployerCvResponse> response = cvService.filterCvsForEmployer(request);
        return ResponseEntity.ok(response);
    }
}