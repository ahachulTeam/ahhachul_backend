package backend.team.ahachul_backend.common.client.dto

class TrainCongestionDto(
    val success: Boolean,
    val code: Int,
    val msg: String = "",
    val data: Train? = null
) {

    class Train(
        val subwayLine: String,
        val trainY: String,
        val congestionResult: Section
    )

    class Section(
        val congestionTrain: String,
        val congestionCar: String,
        val congestionType: Int
    )
}
