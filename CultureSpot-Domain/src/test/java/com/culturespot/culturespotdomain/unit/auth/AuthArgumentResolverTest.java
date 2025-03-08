package com.culturespot.culturespotdomain.unit.auth;

import com.culturespot.culturespotdomain.core.auth.resolver.AuthArgumentResolver;
import com.culturespot.culturespotdomain.core.security.model.CustomUserDetails;
import com.culturespot.culturespotdomain.core.user.entity.User;
import com.culturespot.culturespotdomain.core.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.core.MethodParameter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//  Mockito 테스트 환경 설정
@ExtendWith(MockitoExtension.class)
class AuthArgumentResolverTest {

    @InjectMocks // 실제 테스트할 대상에 Mock객체들을 주입
    private AuthArgumentResolver authArgumentResolver;

    @Mock // 실제 객체를 생성하는 대신 가짜(Mock) 객체를 사용
    private UserRepository userRepository;

    @Mock // 실제 객체를 생성하는 대신 가짜(Mock) 객체를 사용
    private MethodParameter methodParameter;

    @Mock // 실제 객체를 생성하는 대신 가짜(Mock) 객체를 사용
    private Authentication authentication;

    @Mock // 실제 객체를 생성하는 대신 가짜(Mock) 객체를 사용
    private SecurityContext securityContext;

    private User user;

    @BeforeEach
    void setUp() {
        // SecurityContextHolder에 Mock SecurityContext 설정
        SecurityContextHolder.setContext(securityContext);
        // 테스트용 User 객체 생성 (빌더 사용)
        user = User.builder()
                .username("testUser")
                .password("password")
                .role("ROLE_USER")
                .build();
    }

    @Test
    @DisplayName("(1) [핵심 테스트] 정상적인 사용자 인증 테스트")
    void testResolveArgument_WithValidUser_ShouldReturnUser() {
        // Given: CustomUserDetails로 래핑된 User 객체 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // Spring Security의 SecurityContext에서 인증 객체(Authentication)를 가져오는 동작을 가짜로 설정.
        when(securityContext.getAuthentication()).thenReturn(authentication);
        // 사용자가 인증되었다고 가정 (isAuthenticated()가 true 반환).
        when(authentication.isAuthenticated()).thenReturn(true);
        // 증된 사용자의 Principal이 CustomUserDetails 객체라고 설정.
        // 즉, authentication.getPrincipal()이 호출되면 customUserDetails 객체를 반환.
        when(authentication.getPrincipal()).thenReturn(customUserDetails);

        // userRepository.findByUsername("testUser")가 호출되면 테스트용 User 객체를 반환하도록 설정.
        // 실제 데이터베이스를 조회하는 것이 아니라, 가짜 데이터를 반환하는 Mock 동작을 설정.
        // 즉, 컨트롤러에서 User를 주입받을 때 DB 조회가 정상적으로 이루어진다고 가정하는 것.
        // 이 코드가 없으면? -> resolveArgument()에서 userRepository.findByUsername("testUser")가 Optional.empty()를 반환하여 테스트가 실패할 수 있음.
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // When: resolveArgument() 호출
        // 컨트롤러의 인자로 주입될 User 객체를 얻어옴.
        // resolveArgument()는 supportsParameter()에서 true를 반환한 경우 실행되며, 인증된 사용자의 정보를 컨트롤러의 인자로 변환하는 역할
        //  ✔ resolveArgument() 내부에서 하는 작업:
        //      SecurityContextHolder.getContext().getAuthentication() 호출 → Mock 설정된 authentication 객체 반환
        //      authentication.getPrincipal() 호출 → Mock 설정된 CustomUserDetails 객체 반환
        //      customUserDetails.getUsername() 호출 → "testUser" 반환
        //      userRepository.findByUsername("testUser") 호출 → 테스트용 User 객체 반환
        //      User 객체를 반환 → result 변수에 저장됨.
        Object result = authArgumentResolver.resolveArgument(methodParameter,
                new ModelAndViewContainer(), mock(NativeWebRequest.class), mock(WebDataBinderFactory.class));

        // Then: 반환된 객체가 올바른 User 객체인지 확인
        // 반환된 객체가 null이 아님을 확인
        assertNotNull(result);
        // 반환된 객체가 User 타입인지 확인
        assertTrue(result instanceof User);
        // 반환된 User 객체의 username이 "testUser"인지 확인
        assertEquals("testUser", ((User) result).getUsername());
    }

    @Test
    @DisplayName("(2) 인증되지 않은 사용자 예외 처리 테스트")
    void testResolveArgument_WhenUserNotAuthenticated_ShouldThrowSecurityException() {
        // Given: 인증 정보 없음
        when(securityContext.getAuthentication()).thenReturn(null);

        // When & Then: SecurityException 발생 확인
        assertThrows(SecurityException.class, () ->
                authArgumentResolver.resolveArgument(methodParameter,
                        new ModelAndViewContainer(), mock(NativeWebRequest.class), mock(WebDataBinderFactory.class))
        );
    }

    @Test
    @DisplayName("(3) Principal이 CustomUserDetails가 아닌 경우")
    void testResolveArgument_WhenPrincipalIsNotCustomUserDetails_ShouldThrowSecurityException() {
        // Given: Principal이 CustomUserDetails가 아님
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("InvalidPrincipal");

        // When & Then: SecurityException 발생 확인
        assertThrows(SecurityException.class, () ->
                authArgumentResolver.resolveArgument(methodParameter,
                        new ModelAndViewContainer(), mock(NativeWebRequest.class), mock(WebDataBinderFactory.class))
        );
    }

    @Test
    @DisplayName("(4) 데이터베이스에서 사용자를 찾을 수 없는 경우")
    void testResolveArgument_WhenUserNotFoundInDatabase_ShouldThrowIllegalArgumentException() {
        // Given: CustomUserDetails로 래핑된 User 객체
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        // When & Then: IllegalArgumentException 발생 확인
        assertThrows(IllegalArgumentException.class, () ->
                authArgumentResolver.resolveArgument(methodParameter,
                        new ModelAndViewContainer(), mock(NativeWebRequest.class), mock(WebDataBinderFactory.class))
        );
    }
}
