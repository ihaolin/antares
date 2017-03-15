package me.hao0.antares.store.support;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.dto.JobFireTime;
import me.hao0.antares.common.dto.JobInstanceWaitResp;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.exception.JobStateTransferInvalidException;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.App;
import me.hao0.antares.common.model.Job;
import me.hao0.antares.common.model.JobInstance;
import me.hao0.antares.common.model.enums.JobInstanceShardStatus;
import me.hao0.antares.common.model.enums.JobInstanceStatus;
import me.hao0.antares.common.model.enums.JobState;
import me.hao0.antares.common.retry.Retryer;
import me.hao0.antares.common.retry.Retryers;
import me.hao0.antares.common.support.SimpleJobStateMachine;
import me.hao0.antares.common.util.*;
import me.hao0.antares.common.zk.Lock;
import me.hao0.antares.common.zk.NodeListener;
import me.hao0.antares.common.zk.NodeWatcher;
import me.hao0.antares.store.dao.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Job support
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class JobSupport implements DisposableBean {

    @Autowired
    private AntaresZkClient zk;

    @Autowired
    private AppDao appDao;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private JobInstanceDao jobInstanceDao;

    @Autowired
    private JobInstanceShardDao jobInstanceShardDao;

    private final ExecutorService executor;

    /**
     * The retryer for checking job instance finish or not
     */
    private final Retryer<Boolean> checkJobInstanceFinishRetryer = Retryers.get().newRetryer(Predicates.<Boolean>alwaysFalse(), 5);

    public JobSupport(){
        executor = Executors.newExecutor(Systems.cpuNum(), 10000, "JOB-FINISH-CHECKER-");
    }

    /**
     * Trigger the job instance
     * @param appName the app name
     * @param jobClass the job class
     * @param instance the instance
     */
    public void triggerJobInstance(String appName, String jobClass, JobInstance instance) {
        String jobInstancePath = ZkPaths.pathOfJobInstance(appName, jobClass, instance.getId());
        zk.client().create(jobInstancePath, instance.getStatus());
    }

    /**
     * Waiting the job instance finished
     * @param appName the app name
     * @param jobClass the job class
     * @param timeout the timeout seconds to waiting the job instance finished
     * @param jobInstanceId the job instance id
     * @return return the job instance wait response
     */
    public JobInstanceWaitResp waitingJobInstanceFinish(final String appName, final String jobClass, final Long jobInstanceId, long timeout) {

        final CountDownLatch latch = new CountDownLatch(1);

        String jobInstanceNode = ZkPaths.pathOfJobInstance(appName, jobClass, jobInstanceId);

        NodeWatcher watcher = zk.client().newNodeWatcher(jobInstanceNode, new NodeListener() {
            @Override
            public void onDelete() {
                // the job instance has finished
                latch.countDown();
            }
        });

        try {

            Logs.info("Waiting the job({}/{}/{}) with timeout({}) to be finished.", appName, jobClass, jobInstanceId, timeout);

            if (timeout > 0L){
                // need take in account the timeout
                if (!latch.await(timeout, TimeUnit.SECONDS)){
                    return JobInstanceWaitResp.timeout();
                }
            } else {
                // no need take in account the timeout
                latch.await();
            }

        } catch (InterruptedException e) {
            // occur error
            throw new RuntimeException(e);
        } finally {
            if (watcher != null){
                watcher.stop();
            }
        }

        Logs.info("The job({}/{}/{}) has finished.", appName, jobClass, jobInstanceId);

        return JobInstanceWaitResp.success();
    }


    /**
     * Delete the job instance from zk
     * @param appName the app name
     * @param jobClass the job class
     * @param instance the job instance
     * @return return true if finished the job instance, or false
     */
    public Boolean deleteJobInstance(final String appName, final String jobClass, final JobInstance instance){
        return deleteJobInstance(appName, jobClass, instance.getId());
    }

    /**
     * Delete the job instance from zk
     * @param appName the app name
     * @param jobClass the job class
     * @param jobInstanceId the job instance id
     * @return return true if finished the job instance, or false
     */
    public Boolean deleteJobInstance(final String appName, final String jobClass, final Long jobInstanceId){

        // delete the job instance
        String jobInstanceNode = ZkPaths.pathOfJobInstance(appName, jobClass, jobInstanceId);
        zk.client().deleteIfExists(jobInstanceNode);

        return Boolean.TRUE;
    }

    /**
     * Delete all the instances of the job
     * @param appName the app name
     * @param jobClass the job class
     * @return return true if delete successfully, or false
     */
    public Boolean deleteJobInstances(String appName, String jobClass) {

        List<String> instanceIds = findJobInstances(appName, jobClass);
        if (!CollectionUtil.isNullOrEmpty(instanceIds)){
            for (String instanceId : instanceIds){
                deleteJobInstance(appName, jobClass, Long.valueOf(instanceId));
            }
        }

        return Boolean.TRUE;
    }

    /**
     * Find the job instance ids of the job
     * @param appName the app name
     * @param jobClass the job class
     * @return the job instance ids
     */
    public List<String> findJobInstances(String appName, String jobClass){
        String jobInstancesNode = ZkPaths.pathOfJobInstances(appName, jobClass);
        return zk.client().gets(jobInstancesNode);
    }

    /**
     * Update the job fire time info
     * @param appName the app name
     * @param jobClass the job class
     * @param jobFireTime the job fire time
     * @return return true if update successfully, or false
     */
    public Boolean updateJobFireTime(String appName, String jobClass, JobFireTime jobFireTime) {
        String jobFireTimeNode = ZkPaths.pathOfJobFireTime(appName, jobClass);
        zk.client().mkdirs(jobFireTimeNode);
        return zk.client().update(jobFireTimeNode, JSON.toJSONString(jobFireTime));
    }

    /**
     * Get the job fire time info
     * @param appName the app name
     * @param jobClass the job class
     * @return the job fire time info
     */
    public JobFireTime getJobFireTime(String appName, String jobClass){
        String jobFireTimeNode = ZkPaths.pathOfJobFireTime(appName, jobClass);
        if (!zk.client().checkExists(jobFireTimeNode)){
            return null;
        }
        return zk.client().getJson(jobFireTimeNode, JobFireTime.class);
    }

    /**
     * Update the job running state directly
     * @param appName the app name
     * @param jobClass the job class
     * @param state the target state
     * @return return true if update successfully, or false
     */
    public Boolean updateJobStateDirectly(String appName, String jobClass, JobState state){
        String jobStateNode = ZkPaths.pathOfJobState(appName, jobClass);
        zk.client().mkdirs(jobStateNode);
        return zk.client().update(jobStateNode, state.value());
    }

    /**
     * Update the job running state safely, will be constrained by statemachine
     * @param appName the app name
     * @param jobClass the job class
     * @param targetState the new state
     * @return return true if update successfully, or throw JobStateTransferInvalidException
     * @see SimpleJobStateMachine
     * @see JobStateTransferInvalidException
     */
    public Boolean updateJobStateSafely(String appName, String jobClass, JobState targetState){

        JobState currentState = getJobState(appName, jobClass);
        if(!SimpleJobStateMachine.get().allow(currentState, targetState)){
            throw new JobStateTransferInvalidException(appName + "/" + jobClass, currentState, targetState);
        }

        String jobStateNode = ZkPaths.pathOfJobState(appName, jobClass);
        return zk.client().update(jobStateNode, targetState.value());
    }

    /**
     * Check the job state operate valid or not
     * @param appName the app name
     * @param jobClass the job class
     * @param expectState the expect state
     * @param targetState the new state
     * @see JobStateTransferInvalidException
     */
    public void checkJobStateOperate(String appName, String jobClass, JobState expectState, JobState targetState){
        JobState currentState = getJobState(appName, jobClass);
        if ((expectState != null && expectState != currentState)
                || !SimpleJobStateMachine.get().allow(currentState, targetState)){
            throw new JobStateTransferInvalidException(appName + "/" + jobClass, currentState, targetState);
        }
    }

    /**
     * Get the job state
     * @param appName the app name
     * @param jobClass the job class
     * @return the job state
     */
    public JobState getJobState(String appName, String jobClass) {
        String jobStateNode = ZkPaths.pathOfJobState(appName, jobClass);
        if (!zk.client().checkExists(jobStateNode)){
            return JobState.STOPPED;
        }
        return JobState.from(zk.client().getInteger(jobStateNode));
    }

    /**
     * Update the job's scheudler
     * @param appName the app name
     * @param jobClass the job class
     * @param scheduler the scheduler
     * @return return true if update successfully, or false
     */
    public Boolean updateJobScheduler(String appName, String jobClass, String scheduler) {
        String jobSchedulerNode = ZkPaths.pathOfJobScheduler(appName, jobClass);
        zk.client().mkdirs(jobSchedulerNode);
        return zk.client().update(jobSchedulerNode, scheduler);
    }

    /**
     * Get the job scheduler
     * @param appName the app name
     * @param jobClass the job class
     * @return the job scheduler
     */
    public String getJobScheduler(String appName, String jobClass) {
        String jobSchedulerNode = ZkPaths.pathOfJobScheduler(appName, jobClass);
        if (!zk.client().checkExists(jobSchedulerNode)){
            return null;
        }
        return zk.client().getString(jobSchedulerNode);
    }

    /**
     * Make the job instances node
     * @param appName the app name
     * @param jobClass the job class
     * @return return true if make successfully, or false
     */
    public Boolean mkJobInstances(String appName, String jobClass) {
        return zk.client().mkdirs(ZkPaths.pathOfJobInstances(appName, jobClass));
    }

    /**
     * Remove the job from zk
     * @param jobDetail the job detail
     * @return return true if remove successfully, or false
     */
    public Boolean removeJob(JobDetail jobDetail){
        String appJobPath = ZkPaths.pathOfJob(jobDetail.getApp().getAppName(), jobDetail.getJob().getClazz());
        zk.client().deleteRecursivelyIfExists(appJobPath);
        return Boolean.TRUE;
    }

    /**
     * Checking the job is scheduling or not
     * @param appName the app name
     * @param jobClass the job class
     * @return return true if the job is scheduling, or false
     */
    public Boolean checkJobScheduling(String appName, String jobClass) {

        String jobPath = ZkPaths.pathOfJob(appName, jobClass);
        if(!zk.client().checkExists(jobPath)){
            return Boolean.FALSE;
        }

        String scheduler = getJobScheduler(appName, jobClass);
        if(Strings.isNullOrEmpty(scheduler)){
            // The scheduler is empty
            return Boolean.FALSE;
        }

        if(!zk.client().checkExists(ZkPaths.pathOfServer(scheduler))){
            // The scheduler server offline
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * Check the job instance finish or not
     * @param shardFinishDto the shard finish dto
     */
    public void checkJobInstanceFinish(final ShardFinishDto shardFinishDto){
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    checkJobInstanceFinishRetryer.call(new RetryableCheckJobInstanceFinishTask(shardFinishDto));
                    // doCheckJobInstanceFinish(shardFinishDto);
                } catch (Exception e) {
                    Logs.error("failed to check job instance finish({}), cause: {}", shardFinishDto, Throwables.getStackTraceAsString(e));
                }
            }
        });
    }

    /**
     * Check the job has one running job instance
     * @param appName the app name
     * @param jobClass the job class
     * @return return true if has one running job instance, or false
     */
    public boolean hasJobInstance(String appName, String jobClass) {

        String jobInstanceNodePath = ZkPaths.pathOfJobInstances(appName, jobClass);

        List<String> instances = zk.client().gets(jobInstanceNodePath);

        return !CollectionUtil.isNullOrEmpty(instances);
    }

    /**
     * Force to stop the current job instance
     * @param jobDetail the job detail
     * @param finalStatus the final status of the job instance
     */
    public Boolean forceStopJobInstance(JobDetail jobDetail, JobInstanceStatus finalStatus) {

        List<String> jobInstanceIds = findJobInstances(jobDetail.getApp().getAppName(), jobDetail.getJob().getClazz());
        if (!CollectionUtil.isNullOrEmpty(jobInstanceIds)){
            for (String jobInstanceId : jobInstanceIds){
                forceStopJobInstance(jobDetail, Long.valueOf(jobInstanceId), finalStatus);
            }
        }

        return Boolean.TRUE;
    }

    /**
     * Force to stop the current job instance
     * @param jobDetail the job detail
     * @param jobInstanceId the job instance id
     * @param finalStatus the final status
     * @see JobInstanceStatus
     */
    private void forceStopJobInstance(JobDetail jobDetail, Long jobInstanceId, JobInstanceStatus finalStatus) {

        // lock the job instance
        // avoid the job instance finished before
        Lock jobInstanceLock = lockJobInstance(jobInstanceId);
        while (!jobInstanceLock.lock(5000)){
            // lock timeout
            Logs.warn("failed to lock the job instance when force stop job instance(jobDetail={}, jobInstanceId={}).", jobDetail, jobInstanceId);
        }

        try {

            JobInstance instance = jobInstanceDao.findById(jobInstanceId);
            if (JobInstanceStatus.isFinal(instance.getStatus())){
                // job instance is final
                return;
            }

            // try to delete the job instance from zk
            String appName = jobDetail.getApp().getAppName();
            String jobClass = jobDetail.getJob().getClazz();
            if(!deleteJobInstance(appName, jobClass, instance)){
                Logs.warn("failed to delete job instance from zk when force stop job instance((jobDetail={}, jobInstance={})).", instance, jobDetail);
            }

            instance.setUtime(new Date());
            instance.setStatus(finalStatus.value());
            jobInstanceDao.save(instance);

            updateJobStateSafely(appName, jobClass, JobState.WAITING);

        } catch (Exception e){
            Logs.error("failed to force stop job instance(jobDetial={}, jobInstanceId={}), cause: {}",
                    jobDetail, jobInstanceId, Throwables.getStackTraceAsString(e));
        } finally {
            jobInstanceLock.unlock();
        }
    }

    /**
     * The retry task for check job instance finish
     */
    private class RetryableCheckJobInstanceFinishTask implements Callable<Boolean> {

        private final ShardFinishDto shardFinishDto;

        public RetryableCheckJobInstanceFinishTask(ShardFinishDto shardFinishDto) {
            this.shardFinishDto = shardFinishDto;
        }

        @Override
        public Boolean call() throws Exception {
            return doCheckJobInstanceFinish(shardFinishDto);
        }
    }

    /**
     * Check whether the job instance has finished or not
     * @param shardFinishDto the shard finish dto
     * @return return true if check successfully, or false
     */
    private Boolean doCheckJobInstanceFinish(ShardFinishDto shardFinishDto) {

        Long instanceId = shardFinishDto.getInstanceId();

        // loop lock
        // avoid the locked server crashed before finishing the job instance
        Lock jobInstanceLock = lockJobInstance(instanceId);
        while (!jobInstanceLock.lock(5000)){
            // lock timeout
            Logs.warn("failed to lock the job instance(id={}) when check job instance finish, will retry", instanceId);
        }

        // try/catch doesn't impact on the shard finished
        try {

            JobInstance instance = jobInstanceDao.findById(instanceId);
            if (JobInstanceStatus.isFinal(instance.getStatus())){
                // job instance is final
                return Boolean.TRUE;
            }

            // whether all shards are finished
            Integer totalShardCount = jobInstanceShardDao.getJobInstanceTotalShardCount(instanceId);
            Integer successShardCount = jobInstanceShardDao.getJobInstanceStatusShardCount(instanceId, JobInstanceShardStatus.SUCCESS);
            Integer failedShardCount = jobInstanceShardDao.getJobInstanceStatusShardCount(instanceId, JobInstanceShardStatus.FAILED);

            if (Objects.equal(totalShardCount, successShardCount + failedShardCount)){

                // try to delete the job instance from zk
                Job job = jobDao.findById(instance.getJobId());
                App app = appDao.findById(job.getAppId());
                if(!deleteJobInstance(app.getAppName(), job.getClazz(), instance)){
                    Logs.warn("failed to delete job instance({}) from zk when check the job instance finish.", instance);
                }

                // update the job instance
                instance.setEndTime(shardFinishDto.getEndTime());
                if (failedShardCount > 0){
                    // there are shards failed
                    instance.setStatus(JobInstanceStatus.FAILED.value());
                } else {
                    // all shards success
                    instance.setStatus(JobInstanceStatus.SUCCESS.value());
                }
                instance.setUtime(new Date());

                return jobInstanceDao.save(instance);
            }

            return Boolean.TRUE;
        } catch (Exception e){
            Logs.error("failed to check whether the job instance(id={}) has finished, cause: {}",
                    instanceId, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        } finally {
            jobInstanceLock.unlock();
        }
    }

    /**
     * Lock the job instance check finish lock
     * @param jobInstanceId the job instance id
     * @return the lock
     */
    private Lock lockJobInstance(Long jobInstanceId){
        return zk.client().newLock(ZkPaths.pathOfJobInstanceLock(jobInstanceId));
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }
}
