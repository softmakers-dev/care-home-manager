package com.softmakers.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Global
    INTERNAL_SERVER_ERROR(500, "G001", "내부 서버 오류입니다."),
    METHOD_NOT_ALLOWED(405, "G002", "허용되지 않은 HTTP method입니다."),
    INPUT_VALUE_INVALID(400, "G003", "유효하지 않은 입력입니다."),
    INPUT_TYPE_INVALID(400, "G004", "입력 타입이 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(400, "G005", "request message body가 없거나, 값 타입이 올바르지 않습니다."),
    HTTP_HEADER_INVALID(400, "G006", "request header가 유효하지 않습니다."),
    IMAGE_TYPE_NOT_SUPPORTED(400, "G007", "지원하지 않는 이미지 타입입니다."),
    FILE_CONVERT_FAIL(500, "G008", "변환할 수 없는 파일입니다."),
    ENTITY_TYPE_INVALID(500, "G009", "올바르지 않은 entity type 입니다."),
    FILTER_MUST_RESPOND(500, "G010", "필터에서 처리해야 할 요청이 Controller에 접근하였습니다."),

    // Member
    MEMBER_NOT_FOUND(400, "M001", "존재 하지 않는 유저입니다."),
    USERNAME_ALREADY_EXIST(400, "M002", "이미 존재하는 사용자 이름입니다."),
    AUTHENTICATION_FAIL(401, "M003", "로그인이 필요한 화면입니다."),
    AUTHORITY_INVALID(403, "M004", "권한이 없습니다."),
    ACCOUNT_MISMATCH(401, "M005", "계정 정보가 일치하지 않습니다."),
    EMAIL_NOT_CONFIRMED(400, "M007", "인증 이메일 전송을 먼저 해야합니다."),
    PASSWORD_RESET_FAIL(400, "M008", "잘못되거나 만료된 코드입니다."),
    BLOCK_ALREADY_EXIST(400, "M009", "이미 차단한 유저입니다."),
    UNBLOCK_FAIL(400, "M010", "차단하지 않은 유저는 차단해제 할 수 없습니다."),
    BLOCK_MYSELF_FAIL(400, "M011", "자기 자신을 차단 할 수 없습니다."),
    UNBLOCK_MYSELF_FAIL(400, "M012", "자기 자신을 차단해제 할 수 없습니다."),
    PASSWORD_EQUAL_WITH_OLD(400, "M013", "기존 비밀번호와 동일하게 변경할 수 없습니다."),
    LOGOUT_BY_ANOTHER(401, "M014", "다른 기기에 의해 로그아웃되었습니다."),
    ILLEGAL_REGISTRATION_ID(401, "M015", "REGISTRATION ID가 유효하지않습니다.."),

    // Jwt
    JWT_INVALID(401, "J001", "유효하지 않은 토큰입니다."),
    JWT_EXPIRED(401, "J002", "만료된 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(401, "J003", "만료된 REFRESH 토큰입니다. 재로그인 해주십시오."),

    // Email
    EMAIL_SEND_FAIL(500, "E001", "이메일 전송 중 오류가 발생했습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
