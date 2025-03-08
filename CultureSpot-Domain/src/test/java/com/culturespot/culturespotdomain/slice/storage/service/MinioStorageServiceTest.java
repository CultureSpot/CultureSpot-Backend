package com.culturespot.culturespotdomain.slice.storage.service;

import com.culturespot.culturespotdomain.core.storage.property.MinioProperty;
import com.culturespot.culturespotdomain.core.storage.service.MinioStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperty minioProperty;

    @InjectMocks
    private MinioStorageService minioStorageService;

    private MockMultipartFile mockFile1;
    private MockMultipartFile mockFile2;

    @BeforeEach
    void setUp() {
        // 가짜 파일 객체 생성
        mockFile1 = new MockMultipartFile("file1", "test1.jpg", "image/jpeg", "test image content".getBytes());
        mockFile2 = new MockMultipartFile("file2", "test2.png", "image/png", "test image content".getBytes());

        // MinIO 속성 설정
        when(minioProperty.bucketName()).thenReturn("test-bucket");
    }

    @Test
    @DisplayName("단일 파일이 MinIO에 정상적으로 업로드된다")
    void 단일_파일_업로드() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // Given
        Map<String, String> fileMap = Map.of("test1.jpg", "uuid1-test1.jpg");

        // When & Then
        assertDoesNotThrow(() -> minioStorageService.uploadFiles(List.of(mockFile1), fileMap));

        // ✅ MinIO 업로드가 호출되었는지 검증
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("여러 개의 파일이 MinIO에 정상적으로 업로드된다")
    void 다중_파일_업로드() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // Given
        Map<String, String> fileMap = Map.of(
                "test1.jpg", "uuid1-test1.jpg",
                "test2.png", "uuid2-test2.png"
        );

        // When & Then
        assertDoesNotThrow(() -> minioStorageService.uploadFiles(List.of(mockFile1, mockFile2), fileMap));

        // ✅ MinIO 업로드가 각 파일에 대해 호출되었는지 검증
        verify(minioClient, times(2)).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("단일 파일이 MinIO에서 정상적으로 삭제된다")
    void 단일_파일_삭제() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // Given
        String fileName = "uuid1-test1.jpg";

        // When & Then
        assertDoesNotThrow(() -> minioStorageService.deleteFile(fileName));

        // ✅ MinIO 파일 삭제 호출 검증
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("여러 개의 파일이 MinIO에서 정상적으로 삭제된다")
    void 다중_파일_삭제() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // Given
        List<String> fileNames = List.of("uuid1-test1.jpg", "uuid2-test2.png");

        // When & Then
        assertDoesNotThrow(() -> minioStorageService.deleteFiles(fileNames));

        // ✅ MinIO 파일 삭제가 각각 호출되었는지 검증
        verify(minioClient, times(2)).removeObject(any(RemoveObjectArgs.class));
    }
}
