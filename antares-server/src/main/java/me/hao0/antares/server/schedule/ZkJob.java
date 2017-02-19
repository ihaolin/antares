package me.hao0.antares.server.schedule;

import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.dto.JobFireTime;
import me.hao0.antares.common.model.App;
import me.hao0.antares.common.model.Job;
import me.hao0.antares.common.model.enums.JobState;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.store.support.JobSupport;

/**
 * Scheduling the job with zookeeper
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ZkJob extends Component implements Lifecycle, ScheduleJob {

    private final JobSupport jobSupport;

    /**
     * The job detail
     */
    private final JobDetail jobDetail;

    /**
     * The scheduler server
     */
    private final String scheduler;

    /**
     * The job fire time info
     */
    private final JobFireTime jobFireTime;

    public ZkJob(JobSupport jobSupport, JobDetail jobDetail, String scheduler, JobFireTime jobFireTime) {
        this.jobSupport = jobSupport;
        this.jobDetail = jobDetail;
        this.scheduler = scheduler;
        this.jobFireTime = jobFireTime;
    }

    @Override
    public JobDetail getJobDetail() {
        return jobDetail;
    }

    @Override
    public void doStart() {

        // init job nodes
        initJobNodes();
    }

    @Override
    public void doShutdown() {
    }

    /**
     * Init the job info to zookeeper
     */
    private void initJobNodes() {

        App app = jobDetail.getApp();

        Job job = jobDetail.getJob();

        String appName = app.getAppName();
        String jobClass = job.getClazz();

        // create the job instances dir
        jobSupport.mkJobInstances(appName, jobClass);

        // create the job state node
        jobSupport.updateJobStateDirectly(appName, jobClass, JobState.WAITING);

        // create the job scheduler node
        jobSupport.updateJobScheduler(appName, jobClass, scheduler);

        // create the job fireTime info
        jobSupport.updateJobFireTime(appName, jobClass, jobFireTime);
    }
}
