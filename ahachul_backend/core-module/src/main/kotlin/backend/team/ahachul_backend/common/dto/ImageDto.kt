package backend.team.ahachul_backend.common.dto

data class ImageDto(
    val imageId: Long,
    val imageUrl: String,
) {

    companion object {
        fun of(imageId: Long, imageUrl: String): ImageDto {
            return ImageDto(imageId, imageUrl)
        }
    }
}