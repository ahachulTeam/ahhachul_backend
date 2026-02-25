package backend.team.ahachul_backend.common.client

import backend.team.ahachul_backend.common.exception.CommonException
import backend.team.ahachul_backend.common.properties.AwsS3Properties
import backend.team.ahachul_backend.common.response.ResponseCode
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.PutObjectResult
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.springframework.mock.web.MockMultipartFile

class AwsS3ClientTest {

    private val s3Client: AmazonS3Client = Mockito.mock(AmazonS3Client::class.java)
    private val awsS3Properties = AwsS3Properties(
        accessKey = "access-key",
        secretKey = "secret-key",
        bucketName = "test-bucket",
        region = "ap-northeast-2",
    )
    private val awsS3Client = AwsS3Client(s3Client, awsS3Properties)

    @Test
    @DisplayName("ACL 비활성 버킷 호환을 위해 canned ACL 없이 업로드한다.")
    fun uploadWithoutAcl() {
        // given
        val file = MockMultipartFile("files", "sample.png", "image/png", "file-content".toByteArray())
        given(s3Client.putObject(any(PutObjectRequest::class.java)))
            .willReturn(PutObjectResult())
        val putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest::class.java)

        // when
        val uuid = awsS3Client.upload(file)

        // then
        Mockito.verify(s3Client).putObject(putObjectRequestCaptor.capture())
        val request = putObjectRequestCaptor.value
        assertThat(request.bucketName).isEqualTo("test-bucket")
        assertThat(request.key).isEqualTo(uuid)
        assertThat(request.cannedAcl).isNull()
    }

    @Test
    @DisplayName("S3 업로드 실패를 FILE_UPLOAD_FAILED 코드로 변환한다.")
    fun uploadFailure() {
        // given
        val file = MockMultipartFile("files", "sample.png", "image/png", "file-content".toByteArray())
        given(s3Client.putObject(any(PutObjectRequest::class.java)))
            .willThrow(RuntimeException("s3 upload failed"))

        // when & then
        assertThatThrownBy {
            awsS3Client.upload(file)
        }
            .isInstanceOfSatisfying(CommonException::class.java) { commonException ->
                assertThat(commonException.code).isEqualTo(ResponseCode.FILE_UPLOAD_FAILED)
            }
    }

    private fun <T> any(type: Class<T>): T {
        Mockito.any(type)
        return null as T
    }
}
