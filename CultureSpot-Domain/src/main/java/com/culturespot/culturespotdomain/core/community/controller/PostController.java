package com.culturespot.culturespotdomain.core.community.controller;

import com.culturespot.culturespotdomain.core.auth.annotation.Auth;
import com.culturespot.culturespotdomain.core.auth.annotation.MemberOnly;
import com.culturespot.culturespotdomain.core.community.dto.PostCreateRequest;
import com.culturespot.culturespotdomain.core.community.dto.PostModifyRequest;
import com.culturespot.culturespotdomain.core.community.dto.PostSingleResponse;
import com.culturespot.culturespotdomain.core.community.service.PostService;
import com.culturespot.culturespotdomain.core.security.endpoint.EndpointPaths;
import com.culturespot.culturespotdomain.core.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(EndpointPaths.PREFIX_PUBLIC + "/posts/{postId}")
    public PostSingleResponse getPostResponse(
            @PathVariable Long postId
    ) {
        return postService.getPost(postId);
    }


    @MemberOnly
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(EndpointPaths.PREFIX_USER_AUTHENTICATED + "/posts")
    public void createPost(
            @Auth User user,
            @ModelAttribute PostCreateRequest request
    ) {
        postService.createPost(user, request);
    }

    @MemberOnly
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(EndpointPaths.PREFIX_USER_AUTHENTICATED + "/posts/{postId}")
    public void modifyPost(
            @Auth User user,
            @PathVariable Long postId,
            @ModelAttribute PostModifyRequest request
    ) {
        postService.modifyPost(user, postId, request);
    }

    @MemberOnly
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(EndpointPaths.PREFIX_USER_AUTHENTICATED + "/posts/{postId}")
    public void deletePost(
            @Auth User user,
            @PathVariable Long postId
    ) {
        postService.deletePost(user, postId);
    }
}
