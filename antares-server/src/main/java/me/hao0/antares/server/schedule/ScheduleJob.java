package me.hao0.antares.server.schedule;

import me.hao0.antares.common.dto.JobDetail;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ScheduleJob {

    /**
     * Get the job detail
     * @return the job detail
     */
    JobDetail getJobDetail();

}
