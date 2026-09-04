package com.backend.cv_service.util;

import java.util.List;

public class CvPdfTemplateBuilder {

    public static String buildSampleCvHtml(String candidateName, String title, String email, String phone, String address, String summary, List<String> skills, List<String> experiences) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
        sb.append("<title>CV - ").append(escapeXml(candidateName)).append("</title>\n");
        sb.append("<style>\n");
        sb.append("  @page { size: A4; margin: 20mm; }\n");
        sb.append("  body { font-family: 'Noto Sans', sans-serif; font-size: 13px; color: #333333; line-height: 1.5; margin: 0; padding: 0; }\n");
        sb.append("  .header-table { width: 100%; border-bottom: 2px solid #059669; padding-bottom: 15px; margin-bottom: 20px; }\n");
        sb.append("  .name { font-size: 24px; font-weight: 700; color: #111827; margin: 0 0 5px 0; }\n");
        sb.append("  .title { font-size: 14px; font-weight: 600; color: #059669; margin: 0; }\n");
        sb.append("  .contact-info { text-align: right; font-size: 12px; color: #4B5563; }\n");
        sb.append("  .section-title { font-size: 15px; font-weight: 700; color: #111827; border-bottom: 1px solid #E5E7EB; padding-bottom: 4px; margin-top: 20px; margin-bottom: 10px; text-transform: uppercase; }\n");
        sb.append("  .skill-tag {\n");
        sb.append("    display: inline-block;\n");
        sb.append("    background-color: #E5E5E5;\n");
        sb.append("    border-radius: 4px;\n");
        sb.append("    padding: 4px 10px;\n");
        sb.append("    margin: 2px;\n");
        sb.append("    font-size: 12px;\n");
        sb.append("    font-weight: 600;\n");
        sb.append("    color: #374151;\n");
        sb.append("  }\n");
        sb.append("  .item-table { width: 100%; margin-bottom: 12px; }\n");
        sb.append("  .item-title { font-weight: 700; color: #111827; }\n");
        sb.append("  .item-date { text-align: right; font-style: italic; color: #6B7280; font-size: 11px; }\n");
        sb.append("  .item-desc { color: #4B5563; margin-top: 3px; font-size: 12px; }\n");
        sb.append("</style>\n</head>\n<body>\n");

        // Header Table
        sb.append("<table class=\"header-table\">\n<tr>\n");
        sb.append("<td style=\"vertical-align: top;\">\n");
        sb.append("  <div class=\"name\">").append(escapeXml(candidateName)).append("</div>\n");
        sb.append("  <div class=\"title\">").append(escapeXml(title)).append("</div>\n");
        sb.append("</td>\n");
        sb.append("<td class=\"contact-info\" style=\"vertical-align: top;\">\n");
        if (email != null && !email.isBlank()) sb.append("<div>Email: ").append(escapeXml(email)).append("</div>\n");
        if (phone != null && !phone.isBlank()) sb.append("<div>SĐT: ").append(escapeXml(phone)).append("</div>\n");
        if (address != null && !address.isBlank()) sb.append("<div>Địa chỉ: ").append(escapeXml(address)).append("</div>\n");
        sb.append("</td>\n</tr>\n</table>\n");

        // Summary
        if (summary != null && !summary.isBlank()) {
            sb.append("<div class=\"section-title\">Mục tiêu &amp; Giới thiệu</div>\n");
            sb.append("<div style=\"color: #4B5563; font-size: 12px;\">").append(escapeXml(summary)).append("</div>\n");
        }

        // Skills
        if (skills != null && !skills.isEmpty()) {
            sb.append("<div class=\"section-title\">Kỹ năng chuyên môn</div>\n");
            sb.append("<div style=\"margin-top: 6px;\">\n");
            for (String skill : skills) {
                sb.append("  <span class=\"skill-tag\">").append(escapeXml(skill)).append("</span>\n");
            }
            sb.append("</div>\n");
        }

        // Experiences
        if (experiences != null && !experiences.isEmpty()) {
            sb.append("<div class=\"section-title\">Kinh nghiệm làm việc</div>\n");
            for (String exp : experiences) {
                sb.append("<div class=\"item-desc\">• ").append(escapeXml(exp)).append("</div>\n");
            }
        }

        sb.append("</body>\n</html>");
        return sb.toString();
    }

    private static String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
