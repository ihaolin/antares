package me.hao0.antares.server.event.job;

import com.google.common.eventbus.Subscribe;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.server.event.core.EventListener;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.store.service.ServerService;
import me.hao0.antares.store.util.Page;
import me.hao0.antares.store.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * The job event subscriber
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class JobEventListener implements EventListener {

    @Autowired
    private JobService jobService;

    @Autowired
    private ServerService serverService;

    @Subscribe
    public void onJobFinished(JobFinishedEvent e){

        // trigger all next jobs
        notifyAllNextJobs(e);

    }

    private void notifyAllNextJobs(JobFinishedEvent e) {

        int pageNo = 1;
        int pageSize = 100;
        Long jobId = e.getJobId();

        Response<Page<Long>> nextJobIdsResp;
        for (;;){

            nextJobIdsResp = jobService.pagingNextJobIds(jobId, pageNo, pageSize);
            if (!nextJobIdsResp.isSuccess()){
                Logs.error("failed to paging next job ids(jobId={}, pageNo={}, pageSize={}) when notify all next jobs, cause: {}",
                        jobId, pageNo, pageSize, nextJobIdsResp.getErr());
                break;
            }

            List<Long> nextJobIds = nextJobIdsResp.getData().getData();
            if (CollectionUtil.isNullOrEmpty(nextJobIds)){
                // there aren't next jobs
                break;
            }

            for (Long nextJobId : nextJobIds){
                serverService.notifyJob(nextJobId);
            }

            break;
        }
    }
}
