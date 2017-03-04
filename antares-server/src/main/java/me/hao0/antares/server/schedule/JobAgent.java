package me.hao0.antares.server.schedule;


import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.model.enums.JobTriggerType;
import me.hao0.antares.server.schedule.executor.JobExecutor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobAgent implements Job {

    private JobExecutor executor;

    public void setExecutor(JobExecutor executor) {
        this.executor = executor;
    }

    private JobDetail jobDetail;

    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    private JobTriggerType triggerType;

    public void setTriggerType(JobTriggerType triggerType) {
        this.triggerType = triggerType;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        executor.execute(jobDetail, triggerType, context);
    }
}
