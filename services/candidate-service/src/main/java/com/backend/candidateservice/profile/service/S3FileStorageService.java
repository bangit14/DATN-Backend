package com.backend.candidateservice.profile.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class S3FileStorageService {

    private static final long MAX_AVATAR_SIZE = 5L * 1024 * 1024;
    private static final int PRESIGNED_URL_MINUTES = 10;
    private static final Set<String> AVATAR_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    private S3Client s3Client;
    private S3Presigner presigner;

    @PostConstruct
    private void initializeAmazon() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
        this.presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    /** Upload avatar and return only the S3 object key for database storage. */
    public String storeAvatarKey(MultipartFile file) throws IOException {
        validateAvatar(file);

        String extension = extensionOf(file.getOriginalFilename());
        String key = "profile/avatars/" + UUID.randomUUID() + "." + extension;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
            return key;
        } catch (S3Exception exception) {
            log.error("Failed to upload avatar to S3: key={}", key, exception);
            throw new IOException("Unable to upload avatar", exception);
        }
    }

    /**
     * Kept as a compatibility alias for existing callers. It now returns a key,
     * never a public S3 URL.
     */
    @Deprecated
    public String storeFile(MultipartFile file) throws IOException {
        return storeAvatarKey(file);
    }

    /** Build a short-lived URL from an S3 key only. */
    public String presignedUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        String objectKey = extractKey(key);
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_MINUTES))
                .getObjectRequest(builder -> builder.bucket(bucketName).key(objectKey).build())
                .build();
        return presigner.presignGetObject(request).url().toExternalForm();
    }

    public void deleteFile(String keyOrUrl) {
        if (keyOrUrl == null || keyOrUrl.isBlank()) {
            return;
        }
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(extractKey(keyOrUrl))
                    .build());
        } catch (S3Exception exception) {
            log.warn("Unable to delete S3 object: {}", keyOrUrl, exception);
        }
    }

    private void validateAvatar(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Avatar file must not be empty");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new IOException("Avatar file must not exceed 5 MB");
        }

        String extension = extensionOf(file.getOriginalFilename());
        if (!AVATAR_EXTENSIONS.contains(extension)) {
            throw new IOException("Avatar must be JPG, JPEG, PNG or WEBP");
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String extractKey(String keyOrUrl) {
        try {
            URI uri = URI.create(keyOrUrl);
            if (uri.getScheme() == null || uri.getPath() == null) {
                return keyOrUrl;
            }
            return uri.getPath().replaceFirst("^/", "");
        } catch (IllegalArgumentException exception) {
            return keyOrUrl;
        }
    }
}
