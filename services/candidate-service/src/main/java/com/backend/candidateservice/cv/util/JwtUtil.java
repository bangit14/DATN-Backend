package com.backend.candidateservice.cv.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.backend.candidateservice.cv.service.JwtService;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Trích xuất studentId (UUID) từ header "Authorization".
     * @param authorizationHeader Chuỗi đầy đủ, ví dụ: "Bearer eyJhbGciOiJI..."
     * @return UUID của người dùng.
     */
    public UUID extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        // 1. Tách chuỗi "Bearer " để lấy phần token
        String token = authorizationHeader.substring(7);

        try {
            // 2. Giải mã token dùng secret key
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // 👈 thay cho setSigningKey
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();


            // 3. Lấy ra subject ("sub"), thường là ID của người dùng
            String userIdAsString = claims.getSubject();

            // 4. Chuyển đổi từ String sang UUID
            return UUID.fromString(userIdAsString);

        } catch (Exception e) {
            // Xử lý các lỗi giải mã token (hết hạn, không hợp lệ, ...)
            // Trong thực tế, bạn nên log lỗi này ra
            throw new RuntimeException("Unable to parse JWT token", e);
        }
    }
}