package com.culturespot.culturespotcommon.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainExceptionCode {
    USER(1000),
    USER_NOT_FOUND(USER.code + 1, "사용자를 찾을 수 없습니다."),

    POST(2000),
    POST_NOT_FOUND(POST.code + 1, "게시글을 찾을 수 없습니다."),
    POST_DELETE_PERMISSION_DENIED(POST.code + 2, "게시글을 삭제할 권한이 없습니다."),
    POST_EDIT_PERMISSION_DENIED(POST.code +3,"게시글을 수정할 권한이 없습니다."),

    IMAGE(3000),
    IMAGE_EXTENSION_NOT_FOUND(IMAGE.code + 1, "확장자가 존재하지 않는 이미지 파일이 있습니다."),
    UNSUPPORTED_IMAGE_EXTENSIONS(IMAGE.code + 2, "지원하지 않는 이미지 확장자입니다."),

    STORAGE(4000),
    FILE_NOT_FOUND(STORAGE.code + 1, "파일명을 찾을 수 없습니다."),
    FILE_UPLOAD_FAIL(STORAGE.code + 2, "파일 업로드에 실패하였습니다. 관리자에게 문의하세요."),
    FILE_DELETE_FAIL(STORAGE.code + 2, "파일 삭제에 실패했습니다. 관리자에게 문의하세요.");


    private final int code;
    private final String message;

    DomainExceptionCode(int code) {
        this.code = code;
        this.message = "";
    }
}
