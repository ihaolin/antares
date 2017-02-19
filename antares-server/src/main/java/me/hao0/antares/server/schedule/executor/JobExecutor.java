package me.hao0.antares.server.schedule.executor;

import me.hao0.antares.common.dto.JobDetail;
import org.quartz.JobExecutionContext;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobExecutor {

    /**
     * Execute the one job
     * @param jobDetail the job detail
     */
    void execute(JobDetail jobDetail, JobExecutionContext context);
}
