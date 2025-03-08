package com.culturespot.culturespotdomain.core.community.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record PostCreateRequest(
        @JsonUnwrapped
        PostDetails post,
        List<MultipartFile> images
){
    public PostCreateRequest {
        images = (images != null) ? images : List.of();
   }
}
