package backend.team.ahachul_backend.common.domain.entity

import backend.team.ahachul_backend.api.lost.domain.entity.LostPostFileEntity
import jakarta.persistence.*

@Entity
class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    val id: Long = 0,

    var fileName: String,

    var filePath: String,

    @OneToMany(mappedBy = "file", orphanRemoval = true, cascade = [CascadeType.ALL])
    var lostPostFiles: MutableList<LostPostFileEntity> = mutableListOf()

): BaseEntity() {

    companion object {
        fun of(fileName: String, filePath: String): FileEntity {
            return FileEntity(
                fileName = fileName,
                filePath = filePath
            )
        }
    }

    fun addLostPostFile(lostPostFile: LostPostFileEntity) {
        lostPostFiles.add(lostPostFile)
        lostPostFile.file = this
    }
}
