package backend.team.ahachul_backend.schedule.config

import org.quartz.JobDetail
import org.quartz.Trigger

abstract class AbstractJobConfig {

    fun getJobTriggerPair(): Pair<JobDetail, Set<Trigger>> {
        return Pair(getJobDetail(), getTriggers())
    }

    abstract fun getJobDetail(): JobDetail

    abstract fun getTriggers(): Set<Trigger>
}
