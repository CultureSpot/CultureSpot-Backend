package com.culturespot.culturespotdomain.core.community.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostModifyRequest (
        @JsonUnwrapped
        PostDetails postDetails,
        List<MultipartFile> images,
        List<ImageDetailRequest> deleteImage
){
}