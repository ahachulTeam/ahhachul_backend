package backend.team.ahachul_backend.common.response

import org.springframework.http.HttpStatus

enum class ResponseCode(
        val code: String,
        val message: String,
        val httpStatus: HttpStatus
) {
    SUCCESS("100", "SUCCESS", HttpStatus.OK),
    BAD_REQUEST("101", "BAD_REQUEST", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("102", "INTERNAL_SERVER_ERROR", HttpStatus.BAD_REQUEST),
    INVALID_DOMAIN("103", "유효하지 않은 도메인입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ENUM("104", "유효하지 않은 이넘 타입입니다.", HttpStatus.BAD_REQUEST),

    INVALID_APPLE_ID_TOKEN("200", "유효하지 않은 ID 토큰입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN("201", "유효하지 않은 엑세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_ACCESS_TOKEN("202", "유효기간이 만료된 엑세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("203", "유효하지 않은 리프레쉬 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("204", "유효기간이 만료된 리프레쉬 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_OAUTH_AUTHORIZATION_CODE("205", "유효하지 않은 권한 코드입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_OAUTH_ACCESS_TOKEN("206", "유효하지 않은 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH("207", "권한이 없습니다.", HttpStatus.FORBIDDEN),

    // REPORT
    INVALID_REPORT_REQUEST("300", "본인의 게시물은 신고할 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_REPORT_REQUEST("301", "게시물은 유저당 한번만 신고할 수 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REPORT_ACTION("302", "관리자는 유저당 한번만 조취를 취할 수 있습니다.", HttpStatus.BAD_REQUEST),
    BLOCKED_MEMBER("303", "신고로 인해 작성이 제한되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CONDITION_TO_BLOCK_MEMBER("304", "블락 가능한 신고 횟수를 충족하지 않았습니다.", HttpStatus.BAD_REQUEST);
}