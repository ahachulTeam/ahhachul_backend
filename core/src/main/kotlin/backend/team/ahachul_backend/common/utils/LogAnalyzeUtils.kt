package backend.team.ahachul_backend.common.utils

import backend.team.ahachul_backend.common.logging.Logger
import java.util.regex.Pattern

class LogAnalyzeUtils {

    companion object {

        private val logger = Logger(LogAnalyzeUtils::class.java)

        fun extractArgsFromLogStr(log: String, format: String, extractGroup: List<String>): List<String> {
            val logArgs = mutableListOf<String>()
            val matcher = Pattern.compile(format).matcher(log)
            try {
                if (matcher.matches()) {
                    extractGroup.forEach { groupName ->
                        logArgs.add(matcher.group(groupName))
                    }
                }
            } catch (ex: IllegalArgumentException) {
                logger.error("failed to match log pattern : log = $log")
            }
            return logArgs
        }
    }
}
