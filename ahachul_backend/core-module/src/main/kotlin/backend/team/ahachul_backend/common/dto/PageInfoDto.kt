package backend.team.ahachul_backend.common.dto

import backend.team.ahachul_backend.common.utils.PageTokenUtils

data class PageInfoDto<T>(
    val pageToken: String?,
    val data: List<T>,
    val hasNext: Boolean
) {
    companion object {
        fun <T> of(
            data: List<T>,
            pageSize: Int,
            tokenFunctions: Array<(T) -> Any>
        ): PageInfoDto<T> {
            if (data.size <= pageSize) {
                return PageInfoDto(null, data, false)
            }

            val lastValue = data[pageSize - 1]
            val pageTokenValues = tokenFunctions.map { it(lastValue) }
            val pageToken = PageTokenUtils.encodePageToken(*pageTokenValues.toTypedArray())

            return PageInfoDto(pageToken, data.subList(0, pageSize), true)
        }
    }
}
