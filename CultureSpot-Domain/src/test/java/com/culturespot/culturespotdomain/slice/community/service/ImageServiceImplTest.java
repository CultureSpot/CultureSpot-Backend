package com.culturespot.culturespotdomain.slice.community.service;

import com.culturespot.culturespotdomain.core.community.entity.Image;
import com.culturespot.culturespotdomain.core.community.entity.Post;
import com.culturespot.culturespotdomain.core.community.repository.ImageRepository;
import com.culturespot.culturespotdomain.core.community.service.ImageServiceImpl;
import com.culturespot.culturespotdomain.core.storage.service.StorageService;
import com.culturespot.culturespotdomain.core.utils.FileUtils;
import com.culturespot.culturespotdomain.core.utils.validator.MimeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private StorageService storageService; // ✅ StorageService 모킹 추가

    @InjectMocks
    private ImageServiceImpl imageService;

    private Post post;
    private MockMultipartFile mockFile1;
    private MockMultipartFile mockFile2;

    @BeforeEach
    void setUp() {
        // 가짜 Post 객체 생성
        post = Post.builder()
                .title("Test Title")
                .content("Test Content")
                .build();

        // MockMultipartFile 생성 (image/jpeg, image/png)
        mockFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "test image content".getBytes());
        mockFile2 = new MockMultipartFile("image2", "test2.png", "image/png", "test image content".getBytes());
    }

    @Test
    @DisplayName("이미지 업로드 시, 유효한 이미지 리스트를 반환하고 MinIO에 저장된다")
    void 이미지_업로드_성공() {
        // Given
        List<MultipartFile> images = List.of(mockFile1, mockFile2);
        Map<String, String> fileMap = Map.of("uuid1", "test1.jpg", "uuid2", "test2.png");

        // FileUtils와 MimeValidator의 정적 메서드를 모킹
        mockStatic(FileUtils.class);
        mockStatic(MimeValidator.class);

        when(FileUtils.mapUuidToOriginalFileName(images)).thenReturn(fileMap);
        when(imageRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<Image> savedImages = imageService.createImage(post, images);

        // Then (키와 값이 정확한지 검증)
        assertThat(savedImages)
                .extracting(Image::getUploadFileName)
                .containsExactlyInAnyOrder("test1.jpg", "test2.png"); // 🔥 순서 상관없이 검증

        // ✅ MinIO에 업로드했는지 검증
        verify(storageService, times(1)).uploadFiles(images, fileMap);

        // ✅ DB 저장 검증
        verify(imageRepository, times(1)).saveAll(any());
    }


    @Test
    @DisplayName("빈 이미지 리스트가 주어지면, 빈 리스트를 반환한다")
    void 이미지_없음_빈리스트반환() {
        // Given
        List<MultipartFile> images = List.of();

        // When
        List<Image> savedImages = imageService.createImage(post, images);

        // Then
        assertThat(savedImages).isEmpty();
        verify(imageRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("여러 개의 이미지 ID를 삭제하면 MinIO에서도 삭제된다")
    void 여러개_이미지_삭제() {
        // Given
        List<Long> imageIds = List.of(1L, 2L);
        List<Image> images = List.of(
                Image.builder().post(post).uploadFileName("test1.jpg").storedFileName("uuid1").build(),
                Image.builder().post(post).uploadFileName("test2.png").storedFileName("uuid2").build()
        );

        when(imageRepository.findAllById(imageIds)).thenReturn(images);

        // When
        assertDoesNotThrow(() -> imageService.deleteImages(imageIds));

        // ✅ MinIO에서도 삭제되었는지 검증
        verify(storageService, times(1)).deleteFiles(List.of("uuid1", "uuid2"));

        // ✅ DB에서도 삭제되었는지 검증
        verify(imageRepository, times(1)).deleteAll(images);
    }

    @Test
    @DisplayName("단일 이미지 ID를 삭제하면 MinIO에서도 삭제된다")
    void 단일_이미지_삭제() {
        // Given
        Long imageId = 1L;
        Image image = Image.builder().post(post).uploadFileName("test.jpg").storedFileName("uuid").build();

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        // When
        assertDoesNotThrow(() -> imageService.deleteImages(imageId));

        // ✅ MinIO에서 삭제되었는지 검증
        verify(storageService, times(1)).deleteFile("uuid");

        // ✅ DB에서도 삭제되었는지 검증
        verify(imageRepository, times(1)).delete(image);
    }

    @Test
    @DisplayName("존재하지 않는 이미지 ID를 삭제 시, 아무런 동작도 수행되지 않는다")
    void 존재하지않는_이미지ID_삭제_무반응() {
        // Given
        Long imageId = 99L;
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        // When
        assertDoesNotThrow(() -> imageService.deleteImages(imageId));

        // ✅ MinIO에서 삭제가 호출되지 않음
        verify(storageService, never()).deleteFile(any());

        // ✅ DB에서 삭제가 호출되지 않음
        verify(imageRepository, never()).delete(any());
    }
}
