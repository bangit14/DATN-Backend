package com.backend.cv_service.service;

import com.backend.cv_service.exception.CvExportException;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@Slf4j
public class CvPdfExportService {

    private byte[] regularFontBytes;
    private byte[] boldFontBytes;

    public CvPdfExportService() {
        loadFonts();
    }

    private void loadFonts() {
        try {
            ClassPathResource regularRes = new ClassPathResource("fonts/NotoSans-Regular.ttf");
            if (regularRes.exists()) {
                try (InputStream is = regularRes.getInputStream()) {
                    this.regularFontBytes = is.readAllBytes();
                }
            }

            ClassPathResource boldRes = new ClassPathResource("fonts/NotoSans-Bold.ttf");
            if (boldRes.exists()) {
                try (InputStream is = boldRes.getInputStream()) {
                    this.boldFontBytes = is.readAllBytes();
                }
            }
        } catch (Exception e) {
            log.warn("Không thể tải font tiếng Việt từ resources: {}", e.getMessage());
        }
    }

    public byte[] exportToPdf(String htmlContent) {
        if (htmlContent == null || htmlContent.isBlank()) {
            throw new CvExportException("Nội dung HTML của CV không được để trống");
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();

            // Nhúng font tiếng Việt
            if (regularFontBytes != null) {
                builder.useFont(() -> new ByteArrayInputStream(regularFontBytes), "Noto Sans", 400, BaseRendererBuilder.FontStyle.NORMAL, true);
            }
            if (boldFontBytes != null) {
                builder.useFont(() -> new ByteArrayInputStream(boldFontBytes), "Noto Sans", 700, BaseRendererBuilder.FontStyle.NORMAL, true);
            }

            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            log.error("Lỗi xuất CV sang PDF: {}", e.getMessage(), e);
            throw new CvExportException("Xuất PDF thất bại: " + e.getMessage(), e);
        }
    }
}
