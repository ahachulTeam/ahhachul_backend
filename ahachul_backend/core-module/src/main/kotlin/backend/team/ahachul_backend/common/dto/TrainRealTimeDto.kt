package backend.team.ahachul_backend.common.dto

data class TrainRealTimeDto(
    val status: Int?,
    val errorMessage: ErrorMessageDTO?,
    val realtimeArrivalList: List<RealtimeArrivalListDTO>?,
) {
}

data class RealtimeArrivalListDTO(
    val subwayId: String,
    val updnLine: String,
    val trainLineNm: String,
    val statnNm: String?,
    val btrainSttus: String?,
    val barvlDt: String?,
    val btrainNo: String,
    val bstatnNm: String?,
    val recptnDt: String?,
    val arvlMsg2: String,
    val arvlMsg3: String?,
    val arvlCd: String
)

data class ErrorMessageDTO(
    val status: Int?,
    val code: String?,
    val message: String?,
    val link: String?,
    val developerMessage: String?,
    val total: Int
)