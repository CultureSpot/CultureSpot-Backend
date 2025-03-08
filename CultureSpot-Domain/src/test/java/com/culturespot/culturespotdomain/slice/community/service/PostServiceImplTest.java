package com.culturespot.culturespotdomain.slice.community.service;

import com.culturespot.culturespotdomain.core.community.dto.*;
import com.culturespot.culturespotdomain.core.community.entity.Post;
import com.culturespot.culturespotdomain.core.community.repository.PostRepository;
import com.culturespot.culturespotdomain.core.community.service.ImageService;
import com.culturespot.culturespotdomain.core.community.service.PostServiceImpl;
import com.culturespot.culturespotdomain.core.community.validator.PostValidator;
import com.culturespot.culturespotdomain.core.user.entity.User;
import com.culturespot.culturespotdomain.core.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private PostValidator postValidator;  // ✅ Mock 객체로 선언

    @Spy
    private User spyUser;

    @Spy
    private Post spyPost;

    @BeforeEach
    void setUp() {
        // ✅ User 객체 생성 및 spy 적용
        User mockUser = User.builder()
                .username("testUser")
                .build();

        spyUser = spy(mockUser);
        lenient().doReturn(1L).when(spyUser).getUserId();

        // ✅ Post 객체 생성 및 spy 적용 (게시글의 소유자를 spyUser로 설정)
        Post mockPost = Post.builder()
                .user(spyUser)
                .title("Test Title")
                .content("Test Content")
                .build();

        spyPost = spy(mockPost);
        lenient().doReturn(1L).when(spyPost).getPostId();
        lenient().doReturn(spyUser).when(spyPost).getUser(); // ✅ 게시글 소유자를 spyUser로 설정
    }

    @Test
    void 게시글_조회_성공() {
        // given
        when(postRepository.findById(1L)).thenReturn(Optional.of(spyPost));

        // when
        PostSingleResponse result = postService.getPost(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.postDetails().title()).isEqualTo("Test Title");
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void 게시글_생성_성공() {
        // given
        PostCreateRequest request = new PostCreateRequest(
                new PostDetails("New Title", "New Content"),
                List.of()
        );

        when(userRepository.findByUsername(spyUser.getUsername())).thenReturn(Optional.of(spyUser));
        when(postRepository.save(any(Post.class))).thenReturn(spyPost);

        // when
        postService.createPost(spyUser, request);

        // then
        verify(postRepository, times(1)).save(any(Post.class));
        verify(imageService, times(1)).createImage(any(Post.class), anyList());
    }

    @Test
    void 게시글_수정_성공() {
        // given
        PostModifyRequest request = new PostModifyRequest(
                new PostDetails("Updated Title", "Updated Content"),
                List.of(),  // 삭제할 이미지 없음
                List.of()   // 추가할 이미지 없음
        );

        when(postRepository.findById(1L)).thenReturn(Optional.of(spyPost));

        // when
        postService.modifyPost(spyUser, 1L, request);

        // then
        assertThat(spyPost.getTitle()).isEqualTo("Updated Title");
        assertThat(spyPost.getContent()).isEqualTo("Updated Content");
        verify(postRepository, times(1)).save(spyPost);
    }

    @Test
    void 게시글_삭제_성공() {
        // given
        when(postRepository.findById(1L)).thenReturn(Optional.of(spyPost));

        // when
        postService.deletePost(spyUser, 1L);

        // then
        verify(imageService, times(1)).deleteImages(1L);
        verify(postRepository, times(1)).delete(spyPost);
    }

}
