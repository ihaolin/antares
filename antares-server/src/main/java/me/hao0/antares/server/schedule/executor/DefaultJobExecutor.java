package me.hao0.antares.server.schedule.executor;

import com.google.common.base.Throwables;
import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.dto.JobFireTime;
import me.hao0.antares.common.dto.JobInstanceWaitResp;
import me.hao0.antares.common.exception.JobStateTransferInvalidException;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.JobInstance;
import me.hao0.antares.common.model.enums.JobInstanceStatus;
import me.hao0.antares.common.model.enums.JobState;
import me.hao0.antares.common.model.enums.JobTriggerType;
import me.hao0.antares.common.util.Constants;
import me.hao0.antares.common.util.Executors;
import me.hao0.antares.common.util.Systems;
import me.hao0.antares.server.cluster.client.ClientCluster;
import me.hao0.antares.server.cluster.server.ServerHost;
import me.hao0.antares.server.event.core.EventDispatcher;
import me.hao0.antares.server.event.job.JobFailedEvent;
import me.hao0.antares.server.event.job.JobFinishedEvent;
import me.hao0.antares.server.event.job.JobTimeoutEvent;
import me.hao0.antares.server.exception.JobInstanceCreateException;
import me.hao0.antares.store.util.Dates;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.store.support.JobSupport;
import me.hao0.antares.common.util.Response;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class DefaultJobExecutor implements JobExecutor {

    @Autowired
    private JobService jobService;

    @Autowired
    private ServerHost serverHost;

    @Autowired
    private ClientCluster clientCluster;

    @Autowired
    private JobSupport jobSupport;

    @Autowired
    private EventDispatcher eventDispatcher;

    private final ExecutorService asyncExecutor =
            Executors.newExecutor(Systems.cpuNum(), 10000, "DEFAULT-JOB-ASYNC-EXECUTOR-");

    @Override
    public void execute(final JobDetail jobDetail, JobTriggerType triggerType, JobExecutionContext context) {

        final String appName = jobDetail.getApp().getAppName();
        final String jobClass = jobDetail.getJob().getClazz();

        JobInstance instance = null;
        try {

            Logs.info("The job({}/{}) is fired.", appName, jobClass);

            if (triggerType == JobTriggerType.DEFAULT){
                // refresh the job fire time if triggered by scheduler
                asyncExecutor.submit(new RefreshJobFireTimeTask(appName, jobClass, context));
            }

            if (!canRunJobInstance(appName, jobClass)){
                return;
            }

            // job is running
            jobSupport.updateJobStateDirectly(appName, jobClass, JobState.RUNNING);

            // create the job instance and shards
            instance = createInstanceAndShards(jobDetail, triggerType);

            // trigger the clients to pull shards
            jobSupport.triggerJobInstance(appName, jobClass, instance);

            // blocking until all shards to be finished or timeout
            Long timeout = jobDetail.getConfig().getTimeout();
            timeout = timeout == null ? 0L : timeout;
            JobInstanceWaitResp finishResp = jobSupport.waitingJobInstanceFinish(appName, jobClass, timeout, instance.getId());
            if (finishResp.isSuccess()){
                // job instance is finished successfully
                // publish job finish event
                eventDispatcher.publish(new JobFinishedEvent(instance.getJobId(), instance.getId()));
            } else if (finishResp.isTimeout()){
                // job instance is timeout
                eventDispatcher.publish(new JobTimeoutEvent(instance.getJobId(), instance.getId(), buildJobTimeoutDetail(instance)));
            }

            // maybe now the job is paused, stopped, ..., so need to expect the job state
            jobSupport.updateJobStateSafely(appName, jobClass, JobState.WAITING);

            // job has finished
        } catch (JobStateTransferInvalidException e){
            // job state transfer error
            Logs.warn("failed to update job state(instances={}), cause: {}.", instance, e.toString());
        } catch (JobInstanceCreateException e){
            // handle when job instance create failed
            String cause = Throwables.getStackTraceAsString(e);
            Logs.error("failed to create job instance when execute job(jobDetail={}, instance={}), cause: {}",
                    jobDetail, instance, cause);
            handleJobExecuteFailed(jobDetail, instance, appName, jobClass, cause);
        } catch (Exception e){
            // handle other exceptions
            String cause = Throwables.getStackTraceAsString(e);
            Logs.error("failed to execute job(jobDetail={}, instance={}), cause: {}",
                    jobDetail, instance, cause);
            handleJobExecuteFailed(jobDetail, instance, appName, jobClass, cause);
        }
    }

    private String buildJobTimeoutDetail(JobInstance instance) {
        return "startTime：" + Dates.format(instance.getStartTime()) + ", " +
               "elapsedTime：" + Dates.timeIntervalStr(instance.getStartTime(), new Date());
    }

    private boolean canRunJobInstance(String appName, String jobClass) {

        if (!clientCluster.hasAliveClients(appName)){
            // there aren't alive clients
            Logs.warn("Invalid job({}/{}) fired, because there are no available clients.", appName, jobClass);
            return Boolean.FALSE;
        }

        if (jobSupport.hasJobInstance(appName, jobClass)){
            Logs.warn("The job({}/{}) has a running instance, so ignore this execution.", appName, jobClass);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private void handleJobExecuteFailed(JobDetail jobDetail, JobInstance instance, String appName, String jobClass, String cause) {
        try {

            if (instance == null){
                return;
            }

            if (cause.length() > Constants.MAX_ERROR_LENGTH){
                cause = cause.substring(0, Constants.MAX_ERROR_LENGTH);
            }

            // save the job instance
            jobService.failedJobInstance(instance.getId(), cause);

            // delete the instance
            jobSupport.deleteJobInstance(appName, jobClass, instance);

            // update the job state
            jobSupport.updateJobStateDirectly(appName, jobClass, JobState.WAITING);

        } catch (Exception e){
            Logs.error("failed to handle the job(instance={}, appName={}, jobClass={}) execute failed, cause: {}",
                    instance, appName, jobClass, Throwables.getStackTraceAsString(e));
        } finally {

            // publish job failed event
            Long instanceId = instance == null ? null : instance.getId();
            eventDispatcher.publish(new JobFailedEvent(jobDetail.getJob().getId(), instanceId, cause));
        }
    }

    /**
     * Create a new job instance and shards
     * @param detail the job detail
     * @return the new job instance
     */
    private JobInstance createInstanceAndShards(JobDetail detail, JobTriggerType triggerType) {

        JobInstance instance = new JobInstance();
        instance.setJobId(detail.getJob().getId());
        instance.setStatus(JobInstanceStatus.RUNNING.value());
        instance.setTriggerType(triggerType.value());
        instance.setServer(serverHost.get());
        instance.setStartTime(new Date());

        Response<Boolean> saveResp = jobService.createJobInstanceAndShards(instance, detail.getConfig());
        if (!saveResp.isSuccess() || !saveResp.getData()){
            throw new JobInstanceCreateException(saveResp.getErr().toString());
        }

        return instance;
    }

    private class RefreshJobFireTimeTask implements Runnable {

        private final String appName;

        private final String jobClass;

        private final JobExecutionContext context;

        public RefreshJobFireTimeTask(String appName, String jobClass, JobExecutionContext context) {
            this.appName = appName;
            this.jobClass = jobClass;
            this.context = context;
        }

        @Override
        public void run() {
            try {

                // update job fire time info
                JobFireTime jobFireTime = new JobFireTime();

                jobFireTime.setCurrent(Dates.format(context.getFireTime()));
                jobFireTime.setPrev(Dates.format(context.getPreviousFireTime()));
                jobFireTime.setNext(Dates.format(context.getNextFireTime()));

                jobSupport.updateJobFireTime(appName, jobClass, jobFireTime);

            } catch (Exception e){
                Logs.error("failed to execute async task when execute job({}/{}), cause: {}.",
                        appName, jobClass, Throwables.getStackTraceAsString(e));
            }
        }
    }
}
