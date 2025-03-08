package com.culturespot.culturespotdomain.core.community.service;

import com.culturespot.culturespotcommon.global.exception.DomainException;
import com.culturespot.culturespotcommon.global.exception.DomainExceptionCode;
import com.culturespot.culturespotdomain.core.community.dto.*;
import com.culturespot.culturespotdomain.core.community.entity.Image;
import com.culturespot.culturespotdomain.core.community.entity.Post;
import com.culturespot.culturespotdomain.core.community.repository.PostRepository;
import com.culturespot.culturespotdomain.core.community.validator.PostValidator;
import com.culturespot.culturespotdomain.core.user.entity.User;
import com.culturespot.culturespotdomain.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ImageService imageService;

    @Override
    @Transactional(readOnly = true)
    public PostSingleResponse getPost(Long postId) {
        Post findPost = findPostOrThrow(postId);
        return PostSingleResponse.from(findPost);
    }

    @Override
    @Transactional
    public void createPost(User user, PostCreateRequest request) {
        User findUser = findUserOrThrow(user.getUsername());

        PostDetails postDetails = request.post();
        Post post = createEmptyPost(findUser, postDetails);

        List<Image> images = imageService.createImage(post, request.images());
        post.getImages().addAll(images);

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void modifyPost(User user, Long postId, PostModifyRequest request) {
        Post currentPost = findPostOrThrow(postId);

        PostValidator.validateEditPermission(user.getUserId(), currentPost.getPostId());

        Post updatePost = modifyPostDetails(currentPost, request);

        postRepository.save(updatePost);
    }

    @Override
    @Transactional
    public void deletePost(User user, Long postId) {
        Post post = findPostOrThrow(postId);

        PostValidator.validateDeletePermission(user.getUserId(), postId);

        postRepository.delete(post);
        imageService.deleteImages(postId);
    }

    public Post createEmptyPost(User user, PostDetails request) {
        return Post.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .images(new ArrayList<>())
                .build();
    }

    public Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.POST_NOT_FOUND));
    }

    public Post modifyPostDetails(Post post, PostModifyRequest request) {
        PostDetails postDetails = request.postDetails();
        List<ImageDetailRequest> deleteImages = request.deleteImage();
        List<Long> deleteImagesId = deleteImages.stream()
                .map(ImageDetailRequest::imageId)
                .toList();

        if (postDetails.title() != null) {
            post.setTitle(postDetails.title());
        }

        if (postDetails.content() != null) {
            post.setContent(postDetails.content());
        }

        if (request.images() != null && !request.images().isEmpty()) {
            post.setImages(imageService.createImage(post, request.images()));
        }

        if (!deleteImages.isEmpty()) {
            imageService.deleteImages(deleteImagesId);
        }

        return post;
    }

    // UserService로 옮기는거 고민하기
    public User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
    }
}