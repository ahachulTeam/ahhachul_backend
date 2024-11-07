package backend.team.ahachul_backend.api.complaint.application.service

import backend.team.ahachul_backend.api.complaint.application.port.`in`.ComplaintFileUseCase
import backend.team.ahachul_backend.api.complaint.application.port.out.ComplaintMessageFileWriter
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryEntity
import backend.team.ahachul_backend.api.complaint.domain.entity.ComplaintMessageHistoryFileEntity
import backend.team.ahachul_backend.common.client.AwsS3Client
import backend.team.ahachul_backend.common.domain.entity.FileEntity
import backend.team.ahachul_backend.common.persistence.FileWriter
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ComplaintMessageFileService(
    private val complaintMessageFileWriter: ComplaintMessageFileWriter,

    private val fileWriter: FileWriter,

    private val s3Client: AwsS3Client,
    private val s3Utils: AwsS3Utils,
): ComplaintFileUseCase {

    override fun createComplaintMessageFiles(complaint: ComplaintMessageHistoryEntity, files: List<MultipartFile>): List<ImageDto> {
        return files.map {
            val uuid = s3Client.upload(it)
            val s3FileUrl = s3Utils.getUrl(uuid)
            val file = fileWriter.save(
                FileEntity.of(
                    fileName = uuid,
                    filePath = s3FileUrl
                )
            )
            complaintMessageFileWriter.save(
                ComplaintMessageHistoryFileEntity.of(
                    complaintMessage = complaint,
                    file = file
                )
            )
            ImageDto.of(file.id, s3FileUrl)
        }
    }
}
