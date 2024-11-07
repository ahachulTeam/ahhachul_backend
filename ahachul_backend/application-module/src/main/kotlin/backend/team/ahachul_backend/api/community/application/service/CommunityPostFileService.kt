package backend.team.ahachul_backend.api.community.application.service

import backend.team.ahachul_backend.api.community.application.port.`in`.CommunityPostFileUseCase
import backend.team.ahachul_backend.api.community.application.port.out.CommunityPostFileWriter
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostEntity
import backend.team.ahachul_backend.api.community.domain.entity.CommunityPostFileEntity
import backend.team.ahachul_backend.common.client.AwsS3Client
import backend.team.ahachul_backend.common.domain.entity.FileEntity
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.common.persistence.FileReader
import backend.team.ahachul_backend.common.persistence.FileWriter
import backend.team.ahachul_backend.common.utils.AwsS3Utils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CommunityPostFileService(
    private val communityPostFileWriter: CommunityPostFileWriter,

    private val fileWriter: FileWriter,
    private val fileReader: FileReader,

    private val s3Client: AwsS3Client,
    private val s3Utils: AwsS3Utils,
): CommunityPostFileUseCase {

    override fun createCommunityPostFiles(post: CommunityPostEntity, files: List<MultipartFile>): List<ImageDto> {
        return files.map {
            val uuid = s3Client.upload(it)
            val s3FileUrl = s3Utils.getUrl(uuid)
            val file = fileWriter.save(
                FileEntity.of(
                    fileName = uuid,
                    filePath = s3FileUrl
                )
            )
            communityPostFileWriter.save(
                CommunityPostFileEntity.of(
                    post = post,
                    file = file
                )
            )
            ImageDto.of(file.id, s3FileUrl)
        }
    }

    override fun deleteCommunityPostFiles(fileIds: List<Long>) {
        val files = fileReader.findAllIdIn(fileIds)
        files.forEach {
            fileWriter.delete(it.id)
            communityPostFileWriter.deleteByFileId(it.id)
            s3Client.delete(it.fileName)
        }
    }
}
