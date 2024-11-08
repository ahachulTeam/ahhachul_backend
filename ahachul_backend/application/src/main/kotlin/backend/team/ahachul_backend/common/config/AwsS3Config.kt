package backend.team.ahachul_backend.common.config

import backend.team.ahachul_backend.common.properties.AwsS3Properties
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsS3Config(
    private val awsS3Properties: AwsS3Properties
) {

    @Bean
    fun amazonS3Client(): AmazonS3Client {
        val credentials = BasicAWSCredentials(awsS3Properties.accessKey, awsS3Properties.secretKey)
        return AmazonS3ClientBuilder.standard()
            .withRegion(awsS3Properties.region)
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build() as AmazonS3Client
    }
}
