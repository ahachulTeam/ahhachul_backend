package backend.team.ahachul_backend.common.domain.model

enum class YNType {
    Y, N;

    companion object {
        fun convert(bool: Boolean): YNType {
            return when (bool) {
                true -> Y
                false -> N
            }
        }
    }

    fun isY(): Boolean {
        return this == Y
    }

    fun isN(): Boolean {
        return this == N
    }
}
