package com.culturespot.culturespotdomain.core.community.service;

import com.culturespot.culturespotdomain.core.community.entity.Image;
import com.culturespot.culturespotdomain.core.community.entity.Post;
import com.culturespot.culturespotdomain.core.community.repository.ImageRepository;
import com.culturespot.culturespotdomain.core.storage.service.StorageService;
import com.culturespot.culturespotdomain.core.utils.FileUtils;
import com.culturespot.culturespotdomain.core.utils.validator.MimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final List<String> SUPPORTED_IMAGE_MIME = List.of("image/jpeg", "image/png");
    private final StorageService storageService;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public List<Image> createImage(Post post, List<MultipartFile> images) {
        if(images != null && !images.isEmpty()) {
            MimeValidator.validateMime(images, SUPPORTED_IMAGE_MIME);
            Map<String, String> uuidToOriginalFileName = FileUtils.mapUuidToOriginalFileName(images);

            storageService.uploadFiles(images, uuidToOriginalFileName);
            return save(post, uuidToOriginalFileName);
        } else {
            return List.of();
        }
    }

    private List<Image> createImage(Post post, Map<String, String> uuidToOriginalFileName) {
        return uuidToOriginalFileName.entrySet().stream()
                .map(entry -> Image.builder()
                        .storedFileName(entry.getKey()) // UUID 변환된 파일명
                        .uploadFileName(entry.getValue()) // 원본 파일명
                        .post(post) // 해당 게시글
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteImages(List<Long> id) {
        Optional.ofNullable(id)
                .filter(list -> !list.isEmpty())
                .map(imageRepository::findAllById)
                .filter(images -> !images.isEmpty())
                .ifPresent(images -> {
                    // MinIO에서 파일 삭제
                    List<String> fileNames = images.stream()
                            .map(Image::getStoredFileName)
                            .toList();
                    storageService.deleteFiles(fileNames);
                    imageRepository.deleteAll(images);
                });
    }

    @Override
    public void deleteImages(Long id) {
        Optional.ofNullable(id)
                .flatMap(imageRepository::findById)
                .ifPresent(image -> {
                    storageService.deleteFile(image.getStoredFileName());
                    imageRepository.delete(image);
                });
    }


    private List<Image> save(Post post, Map<String, String> uuidToOriginalFileName) {
        return imageRepository.saveAll(createImage(post, uuidToOriginalFileName));
    }
}
