package com.culturespot.culturespotdomain.slice.community.controller;

import com.culturespot.culturespotdomain.core.community.controller.PostController;
import com.culturespot.culturespotdomain.core.community.dto.PostCreateRequest;
import com.culturespot.culturespotdomain.core.community.service.PostService;
import com.culturespot.culturespotdomain.core.security.endpoint.EndpointPaths;
import com.culturespot.culturespotdomain.core.security.jwt.generator.JwtTokenGenerator;
import com.culturespot.culturespotdomain.core.security.jwt.provider.JwtTokenProvider;
import com.culturespot.culturespotdomain.core.user.entity.User;
import com.culturespot.culturespotdomain.core.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)

// JWT 인증을 사용하기 위해 JwtTokenProvider를 테스트 환경에 로드합니다.
// 인증된 사용자에 대한 테스트를 위해 필요합니다.
@Import({JwtTokenProvider.class})
class PostControllerTest {

    //  HTTP 요청을 시뮬레이션하고 컨트롤러를 테스트하는 객체
    @Autowired
    private MockMvc mockMvc;

    // Spring 컨텍스트 내에서 특정 Bean을 Mock 객체로 등록
    @MockBean
    private PostService postService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenGenerator jwtTokenGenerator;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("✅ 인증된 사용자 - 게시글 작성 성공 (`@ModelAttribute` 적용)")

    // Spring Security에서 가짜 인증된 사용자를 생성하여 테스트합니다.
    // username = "testUser", roles = {"USER"} → 이 사용자는 "USER" 역할을 가진 인증된 사용자로 동작합니다.
    @WithMockUser(username = "testUser", roles = {"USER"})
        // 인증된 사용자 시뮬레이션
    void createPost_Success_WithModelAttribute() throws Exception {
        // Given
        // userRepository.findByUsername("testUser") 실행 시, "testUser"라는 사용자 객체를 반환하도록 설정
        // 즉, 데이터베이스에서 해당 사용자가 존재하는 것으로 Mock 처리
        when(userRepository.findByUsername("testUser"))
                .thenReturn(Optional.of(new User("testUser", "password", "ROLE_USER")));

        // 더미 이미지 파일을 생성
        // "images" → @RequestParam("images")로 전달되는 키 값
        // "image.jpg" → 파일 이름.
        // "image/jpeg" → MIME 타입.
        // "dummy-image-content".getBytes() → 파일 내용.
        MockMultipartFile imageFile = new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );

        // 서비스 계층 동작 Mock 설정
        // postService.createPost()가 호출될 때, 아무 동작도 하지 않도록 설정
        // 실제로 데이터베이스에 저장되는 것이 아니라, 테스트에서는 단순히 요청이 정상적으로 전달되는지만 확인
        // 테스트에서 postService.createPost(user, postRequest)를 실행하면, 실제로 서비스 로직이 실행되지 않고 그냥 지나가도록 설정
        doNothing().when(postService).createPost(
                ArgumentMatchers.any(User.class), // createPost(User user, PostRequest postRequest)의 첫 번째 매개변수로 어떤 User 객체가 오든 관계없이 테스트 진행.
                ArgumentMatchers.any(PostCreateRequest.class) // 두 번째 매개변수인 PostRequest에 어떤 값이 오든 상관없이 테스트를 실행
        );

        // ✅ When & Then - 요청 실행 및 검증
        // .file(imageFile): 파일 추가.
        // .param("title", "타이틀 1번"): 제목 전달.
        // .param("content", "컨텐츠 1번"): 본문 전달.
        // .contentType(MediaType.MULTIPART_FORM_DATA): 멀티파트 요청을 처리하도록 설정.
        // .accept(MediaType.APPLICATION_JSON): JSON 응답을 기대.
        // .with(SecurityMockMvcRequestPostProcessors.csrf()): CSRF 토큰 추가.
        // .andExpect(status().isCreated()): 201 Created 상태 코드 검증.
        mockMvc.perform(multipart("/api/users/posts")
                        .file(imageFile)
                        .param("title", "타이틀 1번")
                        .param("content", "컨텐츠 1번")
                        .contentType(MediaType.MULTIPART_FORM_DATA) // 클라이언트가 멀티파트 데이터를 전송하고 있음을 서버에게 알리는 역할
                        .accept(MediaType.APPLICATION_JSON) // "나는 JSON 응답을 받을 거야!", 즉 서버가 JSON 형식으로 응답해야 한다는 의미입니다.
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("🚫 인증되지 않은 사용자 - 게시글 작성 실패 (CSRF 적용)")
    void createPost_Unauthorized_WithCSRF() throws Exception {
        // Given
        String requestBody = """
                    {
                        "post": {
                            "title": "타이틀 1번",
                            "content": "컨텐츠 1번"
                        },
                        "images": []
                    }
                """;

        // JSON 데이터를 멀티파트 요청의 한 부분으로 추가
        MockMultipartFile postRequestPart = new MockMultipartFile(
                "post", "", "application/json", requestBody.getBytes()
        );

        // 더미 이미지 파일 추가 (필요한 경우)
        MockMultipartFile imageFile = new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "dummy-image-content".getBytes()
        );

        // When & Then - 인증되지 않은 사용자가 요청하면 401 Unauthorized 반환
        mockMvc.perform(multipart(EndpointPaths.PREFIX_USER_AUTHENTICATED + "/posts") // ✅ multipart() 사용
                        .file(postRequestPart) // ✅ JSON 데이터를 멀티파트 요청으로 추가
                        .file(imageFile) // ✅ 이미지 파일 포함 (필요한 경우)
                        .contentType(MediaType.MULTIPART_FORM_DATA) // ✅ multipart 요청
                        .accept(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())) // ✅ CSRF 토큰 포함
                .andExpect(status().isUnauthorized()); // ✅ 401 Unauthorized 응답 확인
    }
}
