package backend.team.ahachul_backend.common.response

import org.springframework.http.HttpStatus

enum class ResponseCode(
        val code: String,
        val message: String,
        val httpStatus: HttpStatus
) {
    // COMMON
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
    FAILED_TO_CONNECT_TO_REDIS("208", "외부 통신 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_LOGOUT_TOKEN("209", "이미 로그아웃된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    ALREADY_DELETE_MEMBER("210", "이미 탈퇴된 회원입니다.", HttpStatus.FORBIDDEN),
    INVALID_NICKNAME_FORMAT("211", "닉네임 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME("212", "이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST),

    // REPORT
    INVALID_REPORT_REQUEST("300", "본인의 게시물은 신고할 수 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_REPORT_REQUEST("301", "게시물은 유저당 한번만 신고할 수 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REPORT_ACTION("302", "관리자는 유저당 한번만 조취를 취할 수 있습니다.", HttpStatus.BAD_REQUEST),
    BLOCKED_MEMBER("303", "신고로 인해 작성이 제한되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CONDITION_TO_BLOCK_MEMBER("304", "블락 가능한 신고 횟수를 충족하지 않았습니다.", HttpStatus.BAD_REQUEST),
    EXTERNAL_REPORT_REQUEST("305", "외부 유실물 게시물은 신고할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // LIKE
    ALREADY_LIKED_POST("400", "이미 좋아요한 게시물입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_HATED_POST("401", "이미 싫어요한 게시물입니다.", HttpStatus.BAD_REQUEST),
    REJECT_BY_LIKE_STATUS("402", "좋아요 누른 상태입니다.", HttpStatus.BAD_REQUEST),
    REJECT_BY_HATE_STATUS("403", "싫어요 누른 상태입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_BOOKMARKED_POST("405", "이미 북마크한 게시물입니다.", HttpStatus.BAD_REQUEST),
    NOT_BOOKMARKED_POST("406", "북마크하지 않은 게시물입니다.", HttpStatus.BAD_REQUEST),

    // POST
    IMPOSSIBLE_RECOMMEND_LOST_POST("600", "추천할 수 없는 습득물 게시물입니다.", HttpStatus.BAD_REQUEST),
    POST_NOT_FOUND("404", "게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // TRAIN
    INVALID_PREFIX_TRAIN_NO("700", "유효하지 않은 열차 번호입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_ARRIVAL_TRAIN("701", "열차 도착 정보가 없습니다.", HttpStatus.OK),
    INVALID_SUBWAY_LINE("702", "지원하지 않는 호선입니다.", HttpStatus.BAD_REQUEST),
    INVALID_TRAIN_NO("703", "현재 운행하지 않는 열차 번호입니다.", HttpStatus.BAD_REQUEST),
    FAILED_TO_GET_TRAIN_INFO("704", "현재 열차 정보를 받을 수 없습니다.", HttpStatus.BAD_REQUEST),
    FAILED_TO_GET_CONGESTION_INFO("705", "현재 혼잡도 정보를 받을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // STATION
    EXCEED_MAXIMUM_STATION_COUNT("800", "즐겨찾는 역은 최대 4개까지 가능합니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_PUBLIC_STATION_CODE("801", "공공 지하철 역 코드가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_STATION_TIMES_API("802", "역 시간표 API를 조회하는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_BOOKMARK_STATION("803", "중복된 역은 즐겨찾기에 등록할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_STATION_TIMES_API_RESPONSE("804", "역 시간표 API 응답 값이 올바르지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_TO_GET_STATION_TIMES("805", "현재 역 시간 정보를 받을 수 없습니다.", HttpStatus.NOT_FOUND),
    ROUTE_NOT_FOUND("806", "요청한 경로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_FAVORITE_ROUTE_REQUEST("807", "유효하지 않은 즐겨찾기 경로 요청입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_FAVORITE_ROUTE("808", "이미 등록된 즐겨찾기 경로입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_FAVORITE_ROUTE_STATION("809", "즐겨찾기에 등록된 역만 경로로 지정할 수 있습니다.", HttpStatus.BAD_REQUEST),
    EXCEED_MAXIMUM_FAVORITE_ROUTE_COUNT("810", "즐겨찾기 경로는 최대 10개까지 가능합니다.", HttpStatus.BAD_REQUEST),

    // DELAY PROOF
    DELAY_PROOF_INVALID("900", "유효하지 않은 지연 증빙입니다.", HttpStatus.BAD_REQUEST),
    DELAY_PROOF_EXPIRED("901", "만료된 지연 증빙입니다.", HttpStatus.GONE),
    INVALID_MESSAGE_REQUEST("902", "유효하지 않은 쪽지 요청입니다.", HttpStatus.BAD_REQUEST),
    MESSAGE_ROOM_NOT_FOUND("903", "쪽지방이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    MESSAGE_ROOM_FORBIDDEN("904", "해당 쪽지방에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),

    // FILE
    FILE_READ_FAILED("800", "파일 읽기에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
}
