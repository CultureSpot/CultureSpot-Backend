package com.culturespot.culturespotdomain.core.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUtils {

    public static Map<String, String> mapUuidToOriginalFileName(List<MultipartFile> files) {
        return files.stream()
                .collect(Collectors.toMap(
                        file -> convertToUploadFileName(file) + getFileExtension(file), // 키: UUID 변환 파일명
                        MultipartFile::getOriginalFilename // 값: 원본 파일명
                ));
    }

    private static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return ""; // 확장자가 없는 경우 빈 문자열 반환
    }

    public static List<String> getUploadedFileName(List<MultipartFile> files) {
        return files.stream()
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toList());
    }

    public static List<String> convertToUploadFileName(List<MultipartFile> files) {
        return files.stream()
                .map(file -> convertToUploadFileName(file))
                .collect(Collectors.toList());
    }

    public static String convertToUploadFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return UUID.randomUUID().toString() + extension;
    }
}
