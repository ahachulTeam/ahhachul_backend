package backend.team.ahachul_backend.schedule.config

import backend.team.ahachul_backend.common.constant.CommonConstant
import backend.team.ahachul_backend.schedule.job.RankHashTagJob
import org.quartz.*
import org.springframework.stereotype.Component

@Component
class RankHashTagJobConfig: AbstractJobConfig() {

    override fun getJobDetail(): JobDetail {
        return JobBuilder.newJob()
                .ofType(RankHashTagJob::class.java)
                .usingJobData(getJobDataMap())
                .withIdentity(JobKey("RANK_HASH_TAG_JOB", "RANK"))
                .build()
    }

    private fun getJobDataMap(): JobDataMap {
        val jobDataMap = JobDataMap()
        jobDataMap.put("EXECUTION_COUNT", 0)
        jobDataMap.put("MAX_RETRY_COUNT", 3)
        jobDataMap.put("FILE_READ_PATH", CommonConstant.HASHTAG_FILE_URL)
        return jobDataMap
    }

    override fun getTriggers(): Set<Trigger> {
        return setOf(
                TriggerBuilder.newTrigger()
                        .withIdentity(TriggerKey("RANK_HASH_TAG_TRIGGER", "RANK"))
                        .startNow()
                        .withSchedule(
                                CronScheduleBuilder.cronSchedule("0 0/5 * * * ?")    // 5분 마다 수행
                                        .withMisfireHandlingInstructionFireAndProceed()
                        )
                        .build()
        )
    }
}
