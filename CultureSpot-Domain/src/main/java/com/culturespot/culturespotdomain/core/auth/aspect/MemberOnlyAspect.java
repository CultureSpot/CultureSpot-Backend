package com.culturespot.culturespotdomain.core.auth.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MemberOnlyAspect {

    @Before("@annotation(com.culturespot.culturespotdomain.core.auth.annotation.MemberOnly)")
    public void checkMemberAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("인증된 사용자만 접근 가능합니다.");
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            if (!userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
                throw new SecurityException("회원만 접근 가능합니다.");
            }
        } else {
            throw new SecurityException("잘못된 인증 정보입니다.");
        }
    }
}

