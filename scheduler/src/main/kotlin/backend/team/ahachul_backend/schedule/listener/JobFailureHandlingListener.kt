package backend.team.ahachul_backend.schedule.listener

import backend.team.ahachul_backend.common.logging.Logger
import org.quartz.JobExecutionContext
import org.quartz.Trigger
import org.quartz.TriggerListener
import org.springframework.stereotype.Component


@Component
class JobFailureHandlingListener: TriggerListener {

    companion object {
        const val DEFAULT_MAX_RETRIES: Int = 3
    }

    private val logger: Logger = Logger(javaClass)

    override fun getName(): String? {
        return this::class.simpleName
    }

    override fun triggerFired(trigger: Trigger?, context: JobExecutionContext?) {
        val jobDataMap = context!!.jobDetail.jobDataMap
        jobDataMap.merge("EXECUTION_COUNT", 1) {
            oldValue, initValue -> oldValue as Int + initValue as Int
        }
    }

    override fun vetoJobExecution(trigger: Trigger?, context: JobExecutionContext?): Boolean {
        if (context == null) return false

        val jobDataMap = context.jobDetail.jobDataMap
        val maxRetryCount = context.jobDetail.jobDataMap
            .computeIfAbsent("MAX_RETRY_COUNT") { DEFAULT_MAX_RETRIES } as Int
        val executeCount = jobDataMap.getInt("EXECUTION_COUNT")

        logger.info("${ context.jobDetail.key } Job triggered.. checking retry count: ($executeCount / $maxRetryCount)")

        return when {
            executeCount <= maxRetryCount -> false
            else -> {
                logger.info("Stop trigger Job ${context.jobDetail.key} : $executeCount exceed max count $maxRetryCount")
                true
            }
        }
    }

    override fun triggerMisfired(trigger: Trigger?) {}

    override fun triggerComplete(
        trigger: Trigger?,
        context: JobExecutionContext?,
        triggerInstructionCode: Trigger.CompletedExecutionInstruction?
    ) {
    }
}
