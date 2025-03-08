package com.culturespot.culturespotdomain.core.storage.service;

import com.culturespot.culturespotcommon.global.exception.DomainException;
import com.culturespot.culturespotcommon.global.exception.DomainExceptionCode;
import com.culturespot.culturespotdomain.core.storage.property.MinioProperty;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

// StorageConfig에서 활성화 프로파일에 따른 수동 Bean등록 유도하기 때문에 @Service 없음
@Slf4j
@AllArgsConstructor
public class MinioStorageService implements StorageService {
    private final MinioClient minioClient;
    private final MinioProperty minioProperty;

    @Override
    public List<String> uploadFiles(List<MultipartFile> files, Map<String, String> uuidToOriginalFileName) {
        return uuidToOriginalFileName.keySet().stream()
                .map(uuid -> uploadFile(uuid, findFileByOriginalName(uuidToOriginalFileName.get(uuid), files)))
                .toList();
    }

    private String uploadFile(String uuid, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperty.bucketName())
                            .object(uuid) // 파일명은 UUID 값
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("파일 업로드 성공: {}", uuid);
            return uuid;
        } catch (Exception e) {
            log.error("파일 업로드 실패: {}", uuid, e);
            throw new DomainException(DomainExceptionCode.FILE_UPLOAD_FAIL);
        }
    }

    private MultipartFile findFileByOriginalName(String originalFileName, List<MultipartFile> files) {
        return files.stream()
                .filter(file -> file.getOriginalFilename().equals(originalFileName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다: " + originalFileName));
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperty.bucketName()) // 버킷명 지정
                            .object(fileName) // 삭제할 파일명 지정
                            .build()
            );
            log.info("파일 삭제 성공: {}", fileName);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            throw new DomainException(DomainExceptionCode.FILE_DELETE_FAIL);
        }
    }

    @Override
    public void deleteFiles(List<String> fileNames) {
        fileNames.forEach(this::deleteFile);
    }
}