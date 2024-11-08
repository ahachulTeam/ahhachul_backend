package backend.team.ahachul_backend.common.utils

import backend.team.ahachul_backend.common.properties.AwsS3Properties
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class AwsS3Utils(
    private val s3Properties: AwsS3Properties,
) {

    private var url = ""

    @PostConstruct
    fun init() {
        url = "https://${s3Properties.bucketName}.s3.${s3Properties.region}.amazonaws.com"
    }

    fun getUrl(): String {
        return url
    }

    fun getUrl(uuid: String): String {
        return "$url/$uuid"
    }

    fun extractUuid(uuid: String): String {
        return uuid.split("/").last()
    }
}