package backend.team.ahachul_backend.api.lost.application.service

import backend.team.ahachul_backend.api.lost.application.port.`in`.LostPostFileUseCase
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostEntity
import backend.team.ahachul_backend.api.lost.domain.entity.LostPostFileEntity
import backend.team.ahachul_backend.common.client.AwsS3Client
import backend.team.ahachul_backend.common.domain.entity.FileEntity
import backend.team.ahachul_backend.common.dto.ImageDto
import backend.team.ahachul_backend.common.persistence.FileReader
import backend.team.ahachul_backend.common.persistence.FileWriter
import backend.team.ahachul_backend.common.utils.AwsS3Utils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class LostPostFileService(
    private val fileWriter: FileWriter,
    private val fileReader: FileReader,
    private val s3Client: AwsS3Client,
    private val s3Utils: AwsS3Utils,
): LostPostFileUseCase {

    override fun createLostPostFiles(post: LostPostEntity, files: List<MultipartFile>): List<ImageDto> {
        return files.map {
            val uuid = s3Client.upload(it)
            val s3FileUrl = s3Utils.getUrl(uuid)
            val file = FileEntity.of(
                    fileName = uuid,
                    filePath = s3FileUrl
            )

            saveFile(post, file)
            ImageDto.of(file.id, s3FileUrl)
        }
    }

    override fun deleteLostPostFiles(fileIds: List<Long>) {
        val files = fileReader.findAllIdIn(fileIds)
        files.forEach {
            fileWriter.delete(it.id)
            s3Client.delete(it.fileName)
        }
    }

    private fun saveFile(post: LostPostEntity, file: FileEntity) {
        val lostPostFile = LostPostFileEntity.from(post, file)
        file.addLostPostFile(lostPostFile)
        fileWriter.save(file)
    }
}
