package backend.team.ahachul_backend.common.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authentication (
    val required: Boolean = true,
)

