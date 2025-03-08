package com.culturespot.culturespotdomain.core.utils.validator;

import com.culturespot.culturespotcommon.global.exception.DomainException;
import com.culturespot.culturespotcommon.global.exception.DomainExceptionCode;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class MimeValidator {

    public static final Tika tika = new Tika();

    public static void validateMime(List<MultipartFile> files, List<String> mimeTypes) {
        List<String> invalidFiles = files.stream()
                .filter(file -> {
                    try {
                        return !mimeTypes.contains(tika.detect(file.getInputStream()));
                    } catch (IOException e) {
                        return true; // 파일 읽기 실패도 유효하지 않은 것으로 처리
                    }
                })
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toList());

        if (!invalidFiles.isEmpty()) {
            throw new DomainException(DomainExceptionCode.UNSUPPORTED_IMAGE_EXTENSIONS);
        }
    }
}
