package com.culturespot.culturespotdomain.core.community.service;

import com.culturespot.culturespotdomain.core.community.entity.Image;
import com.culturespot.culturespotdomain.core.community.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    List<Image> createImage(Post post, List<MultipartFile> files);
    void deleteImages(Long id);
    void deleteImages(List<Long> id);
}