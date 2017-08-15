package me.hao0.antares.server.schedule;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.dto.JobFireTime;
import me.hao0.antares.common.exception.JobStateTransferInvalidException;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.enums.JobState;
import me.hao0.antares.common.model.enums.JobTriggerType;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.common.util.Crons;
import me.hao0.antares.server.cluster.server.ServerHost;
import me.hao0.antares.server.schedule.executor.JobExecutor;
import me.hao0.antares.store.util.Dates;
import me.hao0.antares.server.support.Springs;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.store.support.JobSupport;
import me.hao0.antares.common.util.Response;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * The job pool
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@org.springframework.stereotype.Component
public class JobPool extends Component implements Lifecycle, InitializingBean, DisposableBean {

    @Autowired
    private JobService jobService;

    @Autowired
    private ServerHost serverHost;

    @Autowired
    private JobSupport jobSupport;

    @Autowired
    private Springs springs;

    @Value("${antares.scheduleThreadCount:32}")
    private Integer scheduleThreadCount;

    /**
     * The cached job details
     */
    private final Map<String, Map<String, JobDetail>> cacheJobs = Maps.newConcurrentMap();

    /**
     * The zk jobs
     */
    private final Map<String, Map<String, ZkJob>> zkJobs = Maps.newConcurrentMap();

    /**
     * The scheduler factory
     */
    private SchedulerFactory schedulers;

    /**
     * Get app names
     * @return the app names
     */
    public Map<String, Map<String, JobDetail>> getJobs(){
        return ImmutableMap.copyOf(cacheJobs);
    }

    /**
     * Get the job detail
     * @param appName the app name
     * @param jobClass the job class
     * @return the job detail
     */
    public JobDetail getJobDetail(String appName, String jobClass){
        return cacheJobs.get(appName).get(jobClass);
    }

    /**
     * Get the job details of the app
     * @param appName the app name
     * @return the job details of the app
     */
    public List<JobDetail> getJobDetails(String appName){
        return ImmutableList.copyOf(cacheJobs.get(appName).values());
    }

    /**
     * Get the apps
     * @return the apps
     */
    public List<String> getApps(){
        return ImmutableList.copyOf(cacheJobs.keySet());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void doStart() {

        // init the scheduler
        try {
            Properties properties = new Properties();
            properties.setProperty("org.quartz.threadPool.threadCount", scheduleThreadCount + "");
            properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            schedulers = new StdSchedulerFactory(properties);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        // load my jobs
        Response<List<JobDetail>> jobDetailsResp = jobService.findValidJobsByServer(serverHost.get());
        if (!jobDetailsResp.isSuccess()){
            Logs.error("failed to load server({})'s jobs, cause: {}", serverHost.get(), jobDetailsResp.getErr());
        }

        try {

            // initialize all zk jobs
            scheduleJobs(jobDetailsResp.getData());

            // start the scheduler
            schedulers.getScheduler().start();

        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    private void scheduleJobs(List<JobDetail> jobDetails) {

        if (CollectionUtil.isNullOrEmpty(jobDetails)){
            return;
        }

        // init all quartz jobs
        for (JobDetail jobDetail : jobDetails){
            scheduleJob(jobDetail);
        }

    }

    private Boolean scheduleJob(JobDetail jobDetail) {

        try {

            if (!Crons.isValidExpression(jobDetail.getJob().getCron())){
                Logs.warn("The job({})'s cron expression is invalid when schedule", jobDetail);
                return Boolean.FALSE;
            }

            if(createQuartzJob(jobDetail)){
                createCacheJob(jobDetail);
                createZkJob(jobDetail);
                return Boolean.TRUE;
            }

            return Boolean.FALSE;
        } catch (SchedulerException e) {
            Logs.error("failed to schedule job({}), cause: {}",
                    jobDetail, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }
    }

    private void createCacheJob(JobDetail jobDetail) {

        String appName = jobDetail.getApp().getAppName();

        Map<String, JobDetail> appJobs = cacheJobs.get(appName);
        if (CollectionUtil.isNullOrEmpty(appJobs)){
            appJobs = Maps.newConcurrentMap();
            cacheJobs.put(appName, appJobs);
        }

        String jobClazz = jobDetail.getJob().getClazz();
        appJobs.remove(jobClazz);
        appJobs.put(jobClazz, jobDetail);
    }

    private Boolean createQuartzJob(JobDetail jobDetail) throws SchedulerException {

        JobKey jobKey = buildJobKey(jobDetail);

        // one scheduler for one app
        Scheduler scheduler = schedulers.getScheduler();

        // inject the executor
        JobDataMap jobData = buildJobData(jobDetail, JobTriggerType.DEFAULT);

        org.quartz.JobDetail quartzJob = newJob(JobAgent.class)
                .withIdentity(jobKey)
                .usingJobData(jobData)
                .build();

        CronScheduleBuilder scheduleBuilder = cronSchedule(jobDetail.getJob().getCron());
        if (jobDetail.getConfig().getMisfire()){
            // ignore all misfired
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        } else {
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        }

        Trigger trigger = newTrigger()
                .withIdentity(buildTriggerKey(jobDetail))
                .withSchedule(scheduleBuilder)
                .build();

        // start scheduling the job
        scheduler.scheduleJob(quartzJob, trigger);

        return Boolean.TRUE;
    }

    private JobDataMap buildJobData(JobDetail jobDetail, JobTriggerType triggerType) {
        JobDataMap jobData = new JobDataMap();
        jobData.put("executor", springs.getBean(JobExecutor.class));
        jobData.put("jobDetail", jobDetail);
        jobData.put("triggerType", triggerType);
        return jobData;
    }

    private void createZkJob(JobDetail jobDetail) {
        try {
            Scheduler scheduler = schedulers.getScheduler();
            Trigger trigger = scheduler.getTrigger(buildTriggerKey(jobDetail));

            ZkJob zkJob = new ZkJob(jobSupport, jobDetail, serverHost.get(), buildJobFireTime(trigger));
            zkJob.start();
        } catch (Exception e){
            Logs.error("failed to create zk job({}), cause: {}.",
                    jobDetail, Throwables.getStackTraceAsString(e));
        }
    }

    private JobFireTime buildJobFireTime(Trigger trigger) {

        if (trigger == null) {
            return null;
        }

        JobFireTime fireTime = new JobFireTime();

        if (trigger.getPreviousFireTime() != null){
            fireTime.setPrev(Dates.format(trigger.getPreviousFireTime()));
        }

        if (trigger.getNextFireTime() != null){
            fireTime.setNext(Dates.format(trigger.getNextFireTime()));
        }

        return fireTime;
    }

    public Boolean scheduleJob(Long jobId) {

        Response<JobDetail> findResp = jobService.findJobDetailById(jobId);
        if (!findResp.isSuccess()){
            Logs.error("failed to find job detail when schedule job(id={}), cause: {}", jobId, findResp.getErr());
            return Boolean.TRUE;
        }

        scheduleJob(findResp.getData());
        return Boolean.TRUE;
    }

    /**
     * Trigger the job immediately
     * @param jobId the job id
     * @param triggerType the trigger type
     * @see JobTriggerType
     */
    public Boolean triggerJob(Long jobId, JobTriggerType triggerType){

        Response<JobDetail> findResp = jobService.findJobDetailById(jobId);
        if (!findResp.isSuccess()){
            Logs.error("failed to find job detail when trigger job(id={}), cause: {}", jobId, findResp.getErr());
            return Boolean.TRUE;
        }

        return triggerJob(findResp.getData(), triggerType);
    }

    public Boolean triggerJob(JobDetail jobDetail, JobTriggerType triggerType) {

        try {
            JobKey jobKey = buildJobKey(jobDetail);

            Scheduler scheduler = schedulers.getScheduler();
            if(!scheduler.checkExists(jobKey)){
                Logs.warn("The job({}) doesn't exists when trigger job.", jobDetail);
                return Boolean.TRUE;
            }

            scheduler.triggerJob(jobKey, buildJobData(jobDetail, triggerType));

            return Boolean.TRUE;
        } catch (SchedulerException e) {
            Logs.error("failed to trigger job({}), cause: {}.", jobDetail, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }
    }

    /**
     * Pause the job
     * @param jobId the job id
     */
    public Boolean pauseJob(Long jobId){

        Response<JobDetail> findResp = jobService.findJobDetailById(jobId);
        if (!findResp.isSuccess()){
            Logs.error("failed to find job detail when pause job(id={}), cause: {}", jobId, findResp.getErr());
            return Boolean.TRUE;
        }

        return pauseJob(findResp.getData());
    }

    public Boolean pauseJob(JobDetail jobDetail){
        try {

            jobSupport.updateJobStateDirectly(
                    jobDetail.getApp().getAppName(),
                    jobDetail.getJob().getClazz(), JobState.PAUSED);

            JobKey jobKey = buildJobKey(jobDetail);

            Scheduler scheduler = schedulers.getScheduler();
            if(!scheduler.checkExists(jobKey)){
                Logs.warn("The job({}) doesn't exists when pause job.", jobDetail);
                return Boolean.TRUE;
            }

            scheduler.pauseJob(jobKey);

            return Boolean.TRUE;
        } catch (JobStateTransferInvalidException e){
            Logs.warn("failed to transfer job({}) state when pause job: {}", jobDetail, e.toString());
            return Boolean.FALSE;
        } catch (Exception e) {
            Logs.error("failed to pause job({}), cause: {}.", jobDetail, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }
    }

    /**
     * Resume the job schedule
     * @param jobId the job id
     */
    public Boolean resumeJob(Long jobId){

        Response<JobDetail> findResp = jobService.findJobDetailById(jobId);
        if (!findResp.isSuccess()){
            Logs.error("failed to find job detail when resume job(id={}), cause: {}", jobId, findResp.getErr());
            return Boolean.TRUE;
        }

        return resumeJob(findResp.getData());
    }

    public Boolean resumeJob(JobDetail jobDetail){
        try {

            String appName = jobDetail.getApp().getAppName();
            String jobClass = jobDetail.getJob().getClazz();

            jobSupport.updateJobStateDirectly(appName, jobClass, JobState.WAITING);

            JobKey jobKey = buildJobKey(appName, jobClass);

            Scheduler scheduler = schedulers.getScheduler();
            if(!scheduler.checkExists(jobKey)){
                Logs.warn("The job({}) doesn't exists when resume job.", jobDetail);
                return Boolean.TRUE;
            }

            scheduler.resumeJob(jobKey);

            // update job fire time info
            JobFireTime jobFireTime = buildJobFireTime(scheduler.getTrigger(buildTriggerKey(appName, jobClass)));
            if (jobFireTime != null){
                jobSupport.updateJobFireTime(appName, jobClass, jobFireTime);
            }

            return Boolean.TRUE;
        } catch (JobStateTransferInvalidException e){
            Logs.warn("failed to transfer the job({}) state when resume job: {}", jobDetail, e.toString());
            return Boolean.FALSE;
        } catch (Exception e) {
            Logs.error("failed to resume the job({}), cause: {}.", jobDetail, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }
    }

    /**
     * Remove the job from the pool, and will delete the job from zk
     * @param jobId the job id
     */
    public Boolean removeJob(Long jobId){

        Response<JobDetail> findResp = jobService.findJobDetailById(jobId);
        if (!findResp.isSuccess()){
            Logs.error("failed to find the job detail when remove job(id={}), cause: {}", jobId, findResp.getErr());
            return Boolean.TRUE;
        }

        if (removeJob(findResp.getData())){

            // unbind the job from the server
            Response<Boolean> unbindResp = jobService.unbindJobServer(serverHost.get(), jobId);
            if (!unbindResp.isSuccess() || !unbindResp.getData()){
                return Boolean.FALSE;
            }

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * Remove the job from the pool, and will delete the job from zk
     * @param jobDetail the job detail
     */
    public Boolean removeJob(JobDetail jobDetail){
        try {
            JobKey jobKey = buildJobKey(jobDetail);

            Scheduler scheduler = schedulers.getScheduler();
            if(!scheduler.checkExists(jobKey)){
                Logs.warn("The job({}) doesn't exists when remove job.", jobDetail);
                return Boolean.TRUE;
            }

            // delete schedule
            scheduler.deleteJob(jobKey);

            String appName = jobDetail.getApp().getAppName();
            String jobClass = jobDetail.getJob().getClazz();

            jobSupport.updateJobStateDirectly(appName, jobClass, JobState.STOPPED);
            jobSupport.deleteJobInstances(appName, jobClass);



            return Boolean.TRUE;

        } catch (Exception e) {
            Logs.error("failed to remove the job({}), cause: {}.", jobDetail, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }
    }

    /**
     * Reload the job to be scheduled
     * @param jobId the job id
     * @return return true if reload successfully, or not
     */
    public Boolean reloadJob(Long jobId) {
        Response<JobDetail> findResp = jobService.findJobDetailById(jobId);
        if (!findResp.isSuccess()){
            Logs.error("failed to find job detail when remove job(id={}), cause: {}", jobId, findResp.getErr());
            return Boolean.TRUE;
        }

        return reloadJob(findResp.getData());
    }

    /**
     * Reload the job to be scheduled
     * @param jobDetail the job detail
     * @return return true if reload successfully, or not
     */
    private Boolean reloadJob(JobDetail jobDetail) {
        try {

            JobKey jobKey = buildJobKey(jobDetail);

            Scheduler scheduler = schedulers.getScheduler();
            if(!scheduler.checkExists(jobKey)){
                Logs.warn("The job({}) doesn't exists when remove job.", jobDetail);
                return Boolean.TRUE;
            }

            // reload the job
            scheduler.deleteJob(jobKey);
            if(createQuartzJob(jobDetail)){
                createCacheJob(jobDetail);
            }

            return Boolean.TRUE;

        } catch (Exception e) {
            Logs.error("failed to reload the job({}), cause: {}.", jobDetail, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }
    }

    private JobKey buildJobKey(JobDetail jobDetail) {
        return buildJobKey(jobDetail.getApp().getAppName(), jobDetail.getJob().getClazz());
    }

    private JobKey buildJobKey(String appName, String jobClass){
        return JobKey.jobKey(jobClass, appName);
    }

    private TriggerKey buildTriggerKey(JobDetail jobDetail) {
        return buildTriggerKey(jobDetail.getApp().getAppName(), jobDetail.getJob().getClazz());
    }

    private TriggerKey buildTriggerKey(String appName, String jobClass){
        return TriggerKey.triggerKey(jobClass, appName);
    }

    @Override
    public void doShutdown() {
        // shutdown all jobs
        if (!zkJobs.isEmpty()){
            for (Map<String, ZkJob> zkJobMap : zkJobs.values()){
                for (ZkJob zkJob : zkJobMap.values()){
                    zkJob.shutdown();
                }
            }
            zkJobs.clear();
        }
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }


}
