package backend.team.ahachul_backend.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "cloud.aws.credentials")
class AwsS3Properties(
    var accessKey: String = "",
    var secretKey: String = "",
    var bucketName: String = "",
    var region: String = ""
) {
}