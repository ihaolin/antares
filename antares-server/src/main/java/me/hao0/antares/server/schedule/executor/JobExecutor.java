package me.hao0.antares.server.schedule.executor;

import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.model.enums.JobTriggerType;
import org.quartz.JobExecutionContext;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobExecutor {

    /**
     * Execute the one job
     * @param jobDetail the job detail
     * @param triggerType the trigger type
     * @param context the job execute context
     * @see JobTriggerType
     */
    void execute(JobDetail jobDetail, JobTriggerType triggerType, JobExecutionContext context);
}
