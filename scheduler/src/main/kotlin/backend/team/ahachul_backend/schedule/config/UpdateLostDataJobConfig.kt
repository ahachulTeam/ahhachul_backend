package backend.team.ahachul_backend.schedule.config

import backend.team.ahachul_backend.common.constant.CommonConstant
import backend.team.ahachul_backend.schedule.job.UpdateLostDataJob
import org.quartz.*
import org.springframework.stereotype.Component

@Component
class UpdateLostDataJobConfig: AbstractJobConfig() {

    override fun getJobDetail(): JobDetail {
        return JobBuilder.newJob()
                .ofType(UpdateLostDataJob::class.java)
                .usingJobData(getJobDataMap())
                .withIdentity(JobKey("UPDATE_LOST_DATA_JOB", "LOST"))
                .build()
    }

    private fun getJobDataMap(): JobDataMap {
        val jobDataMap = JobDataMap()
        jobDataMap.put("EXECUTION_COUNT", 0)
        jobDataMap.put("MAX_RETRY_COUNT", 3)
        jobDataMap.put("FILE_READ_PATH", CommonConstant.LOST_FILE_URL)
        return jobDataMap
    }

    override fun getTriggers(): Set<Trigger> {
        return setOf(
            TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey("UPDATE_LOST_DATA_TRIGGER ", "LOST"))
                .startNow()
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 0 16 * * ?")    // 매일 밤 새벽 한시(UTC)
                                .withMisfireHandlingInstructionFireAndProceed()
                )
                .build()
        )
    }
}
