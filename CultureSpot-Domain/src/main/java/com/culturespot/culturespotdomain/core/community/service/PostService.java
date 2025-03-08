package com.culturespot.culturespotdomain.core.community.service;

import com.culturespot.culturespotdomain.core.community.dto.PostCreateRequest;
import com.culturespot.culturespotdomain.core.community.dto.PostModifyRequest;
import com.culturespot.culturespotdomain.core.community.dto.PostSingleResponse;
import com.culturespot.culturespotdomain.core.user.entity.User;

public interface PostService {
    PostSingleResponse getPost(Long postId);
    void createPost(User user, PostCreateRequest request);
    void modifyPost(User user, Long postId, PostModifyRequest request);
    void deletePost(User user, Long postId);
}
