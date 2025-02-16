package backend.team.ahachul_backend.common.constant

import java.time.format.DateTimeFormatter

class CommonConstant {
    companion object {
        // HASHTAG
        const val HASHTAG_REDIS_KEY = "hashtag_rank"
        const val LOST_FILE_URL = "/app/ahachul_data/datas/all.json"
        const val HASHTAG_FILE_URL = "logs/archive/hashtag/hashtag_log.log"
        val HASHTAG_LOG_DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
        val CURSOR_PAGING_DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    }
}
