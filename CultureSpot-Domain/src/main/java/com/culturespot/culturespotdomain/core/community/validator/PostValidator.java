package com.culturespot.culturespotdomain.core.community.validator;

import com.culturespot.culturespotcommon.global.exception.DomainException;
import com.culturespot.culturespotcommon.global.exception.DomainExceptionCode;

public class PostValidator {
    /* 수정시 로그인 사용자와 게시글 작성자 일치 확인. */
    public static void validateEditPermission(Long userId, Long postId) {
        if (!userId.equals(postId)) {
            throw new DomainException(DomainExceptionCode.POST_EDIT_PERMISSION_DENIED);
        }
    }

    /* 삭제시 로그인 사용자와 게시글 작성자 일치 확인. */
    public static void validateDeletePermission(Long userId, Long postId) {
        if (!userId.equals(postId)) {
            throw new DomainException(DomainExceptionCode.POST_DELETE_PERMISSION_DENIED);
        }
    }
}
