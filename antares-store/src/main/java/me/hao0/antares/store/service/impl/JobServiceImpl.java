package me.hao0.antares.store.service.impl;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import me.hao0.antares.common.dto.DependenceJob;
import me.hao0.antares.common.dto.JobControl;
import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.dto.JobEditDto;
import me.hao0.antares.common.dto.JobFireTime;
import me.hao0.antares.common.dto.JobInstanceDetail;
import me.hao0.antares.common.dto.JobInstanceDto;
import me.hao0.antares.common.dto.JobInstanceShardDto;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.*;
import me.hao0.antares.common.model.enums.JobInstanceShardStatus;
import me.hao0.antares.common.model.enums.JobInstanceStatus;
import me.hao0.antares.common.model.enums.JobState;
import me.hao0.antares.common.model.enums.JobStatus;
import me.hao0.antares.common.model.enums.JobType;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.common.util.Constants;
import me.hao0.antares.common.util.Executors;
import me.hao0.antares.common.util.Systems;
import me.hao0.antares.store.dao.JobConfigDao;
import me.hao0.antares.store.dao.JobDao;
import me.hao0.antares.store.dao.JobDependenceDao;
import me.hao0.antares.store.dao.JobInstanceDao;
import me.hao0.antares.store.dao.JobInstanceShardDao;
import me.hao0.antares.store.dao.JobServerDao;
import me.hao0.antares.store.exception.JobInstanceNotExistException;
import me.hao0.antares.store.exception.JobNotExistException;
import me.hao0.antares.store.exception.ShardOperateException;
import me.hao0.antares.store.manager.JobConfigManager;
import me.hao0.antares.store.manager.JobManager;
import me.hao0.antares.store.manager.JobInstanceManager;
import me.hao0.antares.store.manager.JobInstanceShardManager;
import me.hao0.antares.store.service.AppService;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.store.support.JobSupport;
import me.hao0.antares.store.util.Dates;
import me.hao0.antares.store.util.Page;
import me.hao0.antares.store.util.Paging;
import me.hao0.antares.store.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * The job service
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobDao jobDao;

    @Autowired
    private JobInstanceDao jobInstanceDao;

    @Autowired
    private JobConfigDao jobConfigDao;

    @Autowired
    private JobServerDao jobServerDao;

    @Autowired
    private JobInstanceShardDao jobInstanceShardDao;

    @Autowired
    private JobDependenceDao jobDependenceDao;

    @Autowired
    private JobManager jobManager;

    @Autowired
    private JobConfigManager jobConfigManager;

    @Autowired
    private JobInstanceManager jobInstanceManager;

    @Autowired
    private JobInstanceShardManager jobInstanceShardManager;

    @Autowired
    private JobSupport jobSupport;

    @Autowired
    private AppService appService;

    private final ExecutorService executor = Executors.newExecutor(Systems.cpuNum(), 10000, "JOB-SERVER-WORKER-");

    @Override
    public Response<Long> saveJob(JobEditDto editing) {
        try {
            JobDetail jobDetail = buildJobDetail(editing);
            Response<Long> saveResp = saveJobDetail(jobDetail);
            if (!saveResp.isSuccess()){
                return Response.notOk(saveResp.getErr());
            }
            return Response.ok(saveResp.getData());
        } catch (JobNotExistException e){
            Logs.warn("The job(id={}) doesn't exist when save job.", e.getId());
            return Response.notOk("job.not.exist");
        } catch (Exception e){
            Logs.error("failed to save job dto({}), cause: {}",
                    editing, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.save.failed");
        }
    }

    private JobDetail buildJobDetail(JobEditDto editing) {

        JobDetail jobDetail = new JobDetail();
        Job job;
        JobConfig config;
        if (editing.getJobId() == null){
            // create
            job = new Job();
            job.setAppId(editing.getAppId());
            job.setType(JobType.DEFAULT.value());
            job.setClazz(editing.getClazz());

            updateJobAttrs(editing, job);

            config = new JobConfig();
            updateJobConfigAttrs(editing, config);

        } else {
            // update
            job = jobDao.findById(editing.getJobId());
            if (job == null){
                throw new JobNotExistException(editing.getJobId());
            }
            updateJobAttrs(editing, job);

            config = jobConfigDao.findByJobId(job.getId());
            updateJobConfigAttrs(editing, config);
        }

        jobDetail.setJob(job);
        jobDetail.setConfig(config);

        return jobDetail;
    }

    private void updateJobConfigAttrs(JobEditDto editing, JobConfig config) {
        config.setParam(editing.getParam());
        config.setShardCount(editing.getShardCount());
        config.setShardParams(editing.getShardParams());
        config.setMaxShardPullCount(editing.getMaxShardPullCount());
        config.setMisfire(editing.getMisfire());
    }

    private void updateJobAttrs(JobEditDto editing, Job job) {
        job.setStatus(editing.getStatus() ? JobStatus.ENABLE.value() : JobStatus.DISABLE.value());
        job.setCron(editing.getCron());
        job.setDesc(editing.getDesc());
    }

    @Override
    public Response<Long> saveJobDetail(JobDetail jobDetail) {
        try {
            Job job = jobDetail.getJob();
            if (jobManager.save(job)){
                JobConfig config = jobDetail.getConfig();
                config.setJobId(job.getId());
                if (jobConfigManager.save(config)){
                    return Response.ok(job.getId());
                } else {
                    // try to rollback the dirty job
                    if (!jobManager.delete(job.getId())){
                        Logs.error("failed to rollback job({}) when save job detail.", job);
                    }
                }
            }
            return Response.ok(job.getId());
        } catch (Exception e){
            Logs.error("failed to save job detail({}), cause: {}",
                            jobDetail, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.save.failed");
        }
    }

    @Override
    public Response<Boolean> deleteJob(final Long jobId) {
        try {
            if (jobManager.delete(jobId)){

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        // maybe produce dirty data if occur failed, but can ignore
                        try {
                            jobConfigManager.deleteByJobId(jobId);
                            jobInstanceManager.deleteByJobId(jobId);
                        } catch (Exception e){
                            Logs.error("failed to delete the job(id={})'s config and instance data.", jobId);
                        }
                    }
                });

                return Response.ok(true);
            }
            return Response.ok(false);
        } catch (Exception e){
            Logs.error("failed to delete job(jobId={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.delete.failed");
        }
    }

    @Override
    public Response<Job> findJobById(Long jobId) {
        try {
            return Response.ok(jobDao.findById(jobId));
        } catch (Exception e){
            Logs.error("failed to find job(jobId={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.find.failed");
        }
    }

    @Override
    public Response<JobDetail> findJobDetailById(Long jobId) {
        try {
            return Response.ok(findJobDetail(jobId, null));
        } catch (Exception e){
            Logs.error("failed to find job detail(jobId={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.find.failed");
        }
    }

    private JobDetail findJobDetail(Long jobId, JobStatus filterStatus){
        Job job = jobDao.findById(jobId);
        if (job == null){
            return null;
        }

        if (filterStatus != null){
            if (!Objects.equal(job.getStatus(), filterStatus.value())){
                return null;
            }
        }

        App app = findAppById(job.getAppId());

        JobConfig config = jobConfigDao.findByJobId(jobId);

        JobDetail jobDetail = new JobDetail();
        jobDetail.setApp(app);
        jobDetail.setJob(job);
        jobDetail.setConfig(config);

        return jobDetail;
    }

    @Override
    public Response<Page<Job>> pagingJob(Long appId, String jobClass, Integer pageNo, Integer pageSize) {
        try {

            // find by the job class full name
            if (!Strings.isNullOrEmpty(jobClass)){
                Job job = jobDao.findByJobClass(appId, jobClass);
                if (job == null){
                    return Response.ok(Page.<Job>empty());
                } else {
                    return Response.ok(new Page<>(1L, Lists.newArrayList(job)));
                }
            }

            // find paging
            Long totalCount = jobDao.countByAppId(appId);
            if (totalCount <= 0L){
                return Response.ok(Page.<Job>empty());
            }

            Paging paging = new Paging(pageNo, pageSize);
            List<Job> jobs = jobDao.listByAppId(appId, paging.getOffset(), paging.getLimit());

            return Response.ok(new Page<>(totalCount, jobs));
        } catch (Exception e){
            Logs.error("failed to paging job (appId={}, jobClass={}, pageNo={}, pageSize={}), cause: {}",
                    appId, jobClass, pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.find.failed");
        }
    }

    @Override
    public Response<Page<JobControl>> pagingJobControl(Long appId, String jobClass, Integer pageNo, Integer pageSize) {
        try {

            Response<Page<Job>> pagingJobResp = pagingJob(appId, jobClass, pageNo, pageSize);
            if(!pagingJobResp.isSuccess()){
                return Response.notOk(pagingJobResp.getErr());
            }

            Page<Job> pagingJob = pagingJobResp.getData();
            if (pagingJob.getTotal() <= 0){
                return Response.ok(Page.<JobControl>empty());
            }

            App app = findAppById(appId);

            Page<JobControl> pagingJobControl = renderJobControls(app.getAppName(), pagingJob);

            return Response.ok(pagingJobControl);

        } catch (Exception e){
            Logs.error("failed to paging job control(appId={}, jobClass={}, pageNo={}, pageSize={}), cause: {}",
                    appId, jobClass, pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.find.failed");
        }
    }

    private Page<JobControl> renderJobControls(final String appName, Page<Job> pagingJob) {

        List<Job> jobs = pagingJob.getData();

        List<JobControl> jobControls = Lists.newCopyOnWriteArrayList();
        for (Job job : jobs){
            jobControls.add(renderJobControl(appName, job));
        }

        return new Page<>(pagingJob.getTotal(), jobControls);
    }

    private JobControl renderJobControl(String appName, Job job) {

        String jobClass = job.getClazz();

        JobControl jobControl = new JobControl();

        jobControl.setId(job.getId());
        jobControl.setClazz(jobClass);
        jobControl.setCron(job.getCron());
        jobControl.setDesc(job.getDesc());

        if (Objects.equal(JobStatus.DISABLE.value(), job.getStatus())){
            // the job is disable
            jobControl.setStateAndDesc(JobState.DISABLE);
            return jobControl;
        }

        if(!jobSupport.checkJobScheduling(appName, jobClass)){
            // the job is enable, but don't be scheduled
            jobControl.setStateAndDesc(JobState.STOPPED);
            return jobControl;
        }

        // use state node as state, but maybe instead of job instances
        JobState jobState = jobSupport.getJobState(appName, jobClass);
        jobControl.setStateAndDesc(jobState);

        if (!JobState.isScheduling(jobState)){
            return jobControl;
        }

        // scheduler
        String scheduler = jobSupport.getJobScheduler(appName, jobClass);
        if (!Strings.isNullOrEmpty(scheduler)){
            jobControl.setScheduler(scheduler);
        }

        // fire time
        JobFireTime jobFireTime = jobSupport.getJobFireTime(appName, jobClass);
        if (jobFireTime != null){
            jobControl.setFireTime(jobFireTime.getCurrent());
            jobControl.setPrevFireTime(jobFireTime.getPrev());
            jobControl.setNextFireTime(jobFireTime.getNext());
        }

        return jobControl;
    }

    @Override
    public Response<Boolean> createJobInstance(JobInstance instance) {
        try {
            return Response.ok(jobInstanceManager.create(instance));
        } catch (Exception e){
            Logs.error("failed to save job instance({}), cause: {}",
                    instance, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.save.failed");
        }
    }

    @Override
    public Response<Boolean> failedJobInstance(Long jobInstanceId, String cause) {

        try {

            JobInstance instance = jobInstanceDao.findById(jobInstanceId);
            if (instance == null){
                return Response.notOk("job.instance.not.exist");
            }

            instance.setStatus(JobInstanceStatus.FAILED.value());
            instance.setCause(cause);
            instance.setEndTime(new Date());

            return Response.ok(jobInstanceDao.save(instance));

        } catch (Exception e){
            Logs.error("failed to failed job instance(id={}, cause={}), cause: {}",
                    jobInstanceId, cause, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.save.failed");
        }
    }

    @Override
    public Response<JobInstance> findJobInstanceById(Long instanceId) {
        try {
            return Response.ok(jobInstanceDao.findById(instanceId));
        } catch (Exception e){
            Logs.error("failed to find job instance(jobInstanceId={}), cause: {}",
                    instanceId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.find.failed");
        }
    }

    @Override
    public Response<Page<JobInstanceDto>> pagingJobInstance(Long appId, String jobClass, Integer pageNo, Integer pageSize) {
        try {

            Long jobId = jobDao.findIdByJobClass(appId, jobClass);
            if (jobId == null){
                return Response.notOk("job.not.exist");
            }

            // find paging
            Long totalCount = jobInstanceDao.countByJobId(jobId);
            if (totalCount <= 0L){
                return Response.ok(Page.<JobInstanceDto>empty());
            }

            Paging paging = new Paging(pageNo, pageSize);
            List<JobInstance> instances = jobInstanceDao.listByJobId(jobId, paging.getOffset(), paging.getLimit());
            List<JobInstanceDto> instanceDtos = renderJobInstanceDtos(instances);

            return Response.ok(new Page<>(totalCount, instanceDtos));
        } catch (Exception e){
            Logs.error("failed to paging job instance(appId={}, jobClass={}, pageNo={}, pageSize={}), cause: {}",
                    appId, jobClass, pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.find.failed");
        }
    }

    private List<JobInstanceDto> renderJobInstanceDtos(List<JobInstance> instances) {

        if (CollectionUtil.isNullOrEmpty(instances)){
            return Collections.emptyList();
        }

        List<JobInstanceDto> instanceDtos = Lists.newArrayListWithExpectedSize(instances.size());

        for (JobInstance instance : instances){
            instanceDtos.add(renderJobInstanceDto(instance));
        }

        return instanceDtos;
    }

    private JobInstanceDto renderJobInstanceDto(JobInstance instance) {

        JobInstanceDto instanceDto = new JobInstanceDto();

        instanceDto.setId(instance.getId());
        instanceDto.setJobId(instance.getJobId());
        instanceDto.setStatus(instance.getStatus());
        instanceDto.setTriggerType(instance.getTriggerType());
        instanceDto.setStartTime(Dates.format(instance.getStartTime()));
        if(instance.getEndTime() != null){
            instanceDto.setEndTime(Dates.format(instance.getEndTime()));
            instanceDto.setCostTime(Dates.timeIntervalStr(instance.getStartTime(), instance.getEndTime()));
        }
        instanceDto.setServer(instance.getServer());
        instanceDto.setCause(instance.getCause());

        return instanceDto;
    }

    @Override
    public Response<Page<JobInstanceShardDto>> pagingJobInstanceShards(Long jobInstanceId, Integer pageNo, Integer pageSize) {
        try {

            // find paging
            Long totalCount = jobInstanceShardDao.countByInstanceId(jobInstanceId);
            if (totalCount <= 0L){
                return Response.ok(Page.<JobInstanceShardDto>empty());
            }

            Paging paging = new Paging(pageNo, pageSize);
            List<JobInstanceShard> shards =
                    jobInstanceShardDao.listByInstanceId(jobInstanceId, paging.getOffset(), paging.getLimit());

            List<JobInstanceShardDto> shardDtos = renderJobInstanceShardDtos(shards);

            return Response.ok(new Page<>(totalCount, shardDtos));

        } catch (Exception e){
            Logs.error("failed to paging job instance progress(jobInstanceId={}, pageNo={}, pageSize={}), cause: {}",
                    jobInstanceId, pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.shard.find.failed");
        }
    }

    private List<JobInstanceShardDto> renderJobInstanceShardDtos(List<JobInstanceShard> shards) {

        if (CollectionUtil.isNullOrEmpty(shards)){
            return Collections.emptyList();
        }

        List<JobInstanceShardDto> shardDtos = Lists.newArrayListWithExpectedSize(shards.size());

        for (JobInstanceShard shard : shards){
            shardDtos.add(renderJobInstanceShardDto(shard));
        }

        return shardDtos;
    }

    private JobInstanceShardDto renderJobInstanceShardDto(JobInstanceShard shard) {
        JobInstanceShardDto shardDto = new JobInstanceShardDto();

        shardDto.setId(shard.getId());
        shardDto.setInstanceId(shard.getInstanceId());
        shardDto.setStatus(shard.getStatus());
        shardDto.setItem(shard.getItem());
        shardDto.setParam(shard.getParam());
        shardDto.setPullTime(Dates.format(shard.getPullTime()));
        shardDto.setStartTime(Dates.format(shard.getStartTime()));
        shardDto.setEndTime(Dates.format(shard.getEndTime()));
        shardDto.setPullClient(shard.getPullClient());
        shardDto.setPullCount(shard.getPullCount());
        shardDto.setFinishClient(shard.getFinishClient());
        shardDto.setCause(shard.getCause());

        return shardDto;
    }

    @Override
    public Response<List<Long>> findJobIdsByServer(String server) {
        try {
            return Response.ok(jobServerDao.findJobsByServer(server));
        } catch (Exception e){
            Logs.error("failed to find jobs by server(server={}), cause: {}",
                    server, Throwables.getStackTraceAsString(e));
            return Response.notOk("server.find.job.failed");
        }
    }

    @Override
    public Response<List<Job>> findJobsByServer(String server) {
        try {
            List<Long> jobIds = jobServerDao.findJobsByServer(server);
            if (jobIds == null || jobIds.isEmpty()){
                return Response.ok(Collections.<Job>emptyList());
            }
            return Response.ok(jobDao.findByIds(jobIds));
        } catch (Exception e){
            Logs.error("failed to find jobs by server(server={}), cause: {}",
                    server, Throwables.getStackTraceAsString(e));
            return Response.notOk("server.find.job.failed");
        }
    }

    @Override
    public Response<List<JobDetail>> findValidJobsByServer(String server) {
        try {
            List<Long> jobIds = jobServerDao.findJobsByServer(server);
            if (jobIds == null || jobIds.isEmpty()){
                return Response.ok(Collections.<JobDetail>emptyList());
            }
            List<JobDetail> details = Lists.newArrayListWithExpectedSize(jobIds.size());
            JobDetail jobDetail;
            for (Long jobId : jobIds){
                jobDetail = findJobDetail(jobId, JobStatus.ENABLE);
                if (jobDetail != null){
                    details.add(jobDetail);
                }
            }
            return Response.ok(details);
        } catch (Exception e){
            Logs.error("failed to find jobs by server(server={}), cause: {}",
                    server, Throwables.getStackTraceAsString(e));
            return Response.notOk("server.find.job.failed");
        }
    }

    @Override
    public Response<Boolean> removeAllJobsByServer(String server) {
        try {
            return Response.ok(jobServerDao.unbindJobsOfServer(server));
        } catch (Exception e){
            Logs.error("failed to find jobs by server(server={}), cause: {}",
                    server, Throwables.getStackTraceAsString(e));
            return Response.notOk("server.remove.job.failed");
        }
    }

    @Override
    public Response<Boolean> bindJob2Server(Long jobId, String server) {
        try {

            // try to unbind the old server
            jobServerDao.unbindJob(jobId);

            // bind to the new server
            JobServer jobServer = new JobServer();
            jobServer.setJobId(jobId);
            jobServer.setServer(server);

            return Response.ok(jobServerDao.bind(jobServer));

        } catch (Exception e){
            Logs.error("failed to find jobs by server(server={}), cause: {}",
                    server, Throwables.getStackTraceAsString(e));
            return Response.notOk("server.bind.job.failed");
        }
    }

    @Override
    public Response<String> findServerOfJob(Long jobId) {
        try {
            return Response.ok(jobServerDao.findServerByJobId(jobId));
        } catch (Exception e){
            Logs.error("failed to find server of the job(id={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.find.server.failed");
        }
    }

    @Override
    public Response<JobConfig> findJobConfigByJobId(Long jobId) {
        try {
            return Response.ok(jobConfigDao.findByJobId(jobId));
        } catch (Exception e){
            Logs.error("failed to find config of the job(jobId={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.config.find.failed");
        }
    }

    @Override
    public Response<Boolean> disableJob(Long jobId) {
        return updateJobStatus(jobId, JobStatus.DISABLE);
    }

    @Override
    public Response<Boolean> enableJob(Long jobId) {
        return updateJobStatus(jobId, JobStatus.ENABLE);
    }

    @Override
    public Response<JobInstanceDetail> monitorJobInstanceDetail(Long jobId) {
        try {

            Long instanceId =jobInstanceDao.findMaxId(jobId);
            if (instanceId == null){
                return Response.notOk("job.not.running");
            }

            return Response.ok(renderJobRunningInstance(instanceId));

        } catch (JobInstanceNotExistException e){
            return Response.notOk("job.instance.not.exist");
        } catch (Exception e){
            Logs.error("failed to monitor the job instance detail(jobId={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.detail.monitor.failed");
        }
    }

    @Override
    public Response<JobInstanceDetail> findJobInstanceDetail(Long jobInstanceId) {
        try {
            return Response.ok(renderJobRunningInstance(jobInstanceId));
        } catch (JobInstanceNotExistException e){
            return Response.notOk("job.instance.not.exist");
        } catch (Exception e){
            Logs.error("failed to find the job instance detail(jobInstanceId={}), cause: {}",
                    jobInstanceId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.detail.find.failed");
        }
    }

    @Override
    public Response<Boolean> forceFinishJob(Long jobId) {
        try {

            JobDetail jobDetail = findJobDetail(jobId, null);
            if (jobDetail == null){
                return Response.notOk("job.not.exist");
            }

            String appName = jobDetail.getApp().getAppName();
            String jobClass = jobDetail.getJob().getClazz();

            jobSupport.deleteJobInstances(appName, jobClass);

            return Response.ok(true);
        } catch (Exception e){
            Logs.error("failed to force finish the job(id={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("operate.failed");
        }
    }

    @Override
    public Response<Boolean> unbindJobServer(String server, Long jobId) {
        try {
            return Response.ok(jobServerDao.unbindJob(jobId));
        } catch (Exception e){
            Logs.error("failed to unbind the job server(server={}, jobId={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("operate.failed");
        }
    }

    @Override
    public Response<Boolean> addJobDependence(JobDependence dependence) {
        try {

            Job nextJob = jobDao.findById(dependence.getNextJobId());
            if (nextJob == null){
                return Response.notOk("job.not.exist");
            }

            return Response.ok(jobDependenceDao.addDependence(dependence));
        } catch (Exception e){
            Logs.error("failed to add the job dependence({}), cause: {}",
                    dependence, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.dependence.add.failed");
        }
    }

    @Override
    public Response<Boolean> deleteNextJob(Long jobId, Long nextJobId) {
        try {
            return Response.ok(jobDependenceDao.deleteNextJobId(jobId, nextJobId));
        } catch (Exception e){
            Logs.error("failed to delete the job dependence(jobId={}, nextJobId={}), cause: {}",
                    jobId, nextJobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.dependence.delete.failed");
        }
    }

    @Override
    public Response<Boolean> deleteNextJobs(Long jobId) {
        try {
            return Response.ok(jobDependenceDao.deleteNextJobIds(jobId));
        } catch (Exception e){
            Logs.error("failed to delete the job dependences(jobId={}), cause: {}",
                    jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.dependence.delete.failed");
        }
    }

    @Override
    public Response<Page<DependenceJob>> pagingNextJobs(Long jobId, Integer pageNo, Integer pageSize) {
        try {

            Paging paging = new Paging(pageNo, pageSize);

            Page<Long> pagingJobIds = jobDependenceDao.pagingNextJobIds(jobId, paging.getOffset(), paging.getLimit());
            if (pagingJobIds.getTotal() <= 0L){
                return Response.ok(Page.<DependenceJob>empty());
            }

            return Response.ok(renderDependenceJobs(pagingJobIds));

        } catch (Exception e){
            Logs.error("failed to paging the next jobs(jobId={}, pageNo={}, pageSize={}), cause: {}",
                    jobId, pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.dependence.find.failed");
        }
    }

    @Override
    public Response<Page<Long>> pagingNextJobIds(Long jobId, Integer pageNo, Integer pageSize) {
        try {

            Paging paging = new Paging(pageNo, pageSize);

            Page<Long> pagingJobIds = jobDependenceDao.pagingNextJobIds(jobId, paging.getOffset(), paging.getLimit());

            return Response.ok(pagingJobIds);

        } catch (Exception e){
            Logs.error("failed to paging the next job ids(jobId={}, pageNo={}, pageSize={}), cause: {}",
                    jobId, pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.dependence.find.failed");
        }
    }

    private Page<DependenceJob> renderDependenceJobs(Page<Long> pagingJobIds) {

        List<DependenceJob> dependenceJobs = Lists.newArrayListWithCapacity(pagingJobIds.getData().size());

        DependenceJob dependenceJob;
        for (Long jobId : pagingJobIds.getData()){
            dependenceJob = renderDependenceJob(jobId);
            if (dependenceJob != null) {
                dependenceJobs.add(dependenceJob);
            }
        }

        return new Page<>(pagingJobIds.getTotal(), dependenceJobs);
    }

    private DependenceJob renderDependenceJob(Long jobId) {

        Job job = jobDao.findById(jobId);
        if (job == null){
            Logs.warn("The job(id={}) doesn't exist when render dependence job.", jobId);
            return null;
        }

        App app = findAppById(job.getAppId());

        DependenceJob dependenceJob = new DependenceJob();
        dependenceJob.setId(jobId);
        dependenceJob.setAppName(app.getAppName());
        dependenceJob.setJobClass(job.getClazz());

        return dependenceJob;
    }

    private JobInstanceDetail renderJobRunningInstance(Long instanceId) {

        JobInstance instance = jobInstanceDao.findById(instanceId);
        if (instance == null){
            throw new JobInstanceNotExistException();
        }

        JobInstanceDetail runningInstance = new JobInstanceDetail();

        // job instance info
        runningInstance.setJobId(instance.getJobId());
        runningInstance.setInstanceId(instanceId);
        runningInstance.setStatus(instance.getStatus());
        runningInstance.setStartTime(Dates.format(instance.getStartTime()));
        if (instance.getEndTime() != null){
            runningInstance.setEndTime(Dates.format(instance.getEndTime()));
        }

        // progress info
        Integer totalShardCount = jobInstanceShardDao.countByInstanceId(instanceId).intValue();
        runningInstance.setTotalShardCount(totalShardCount);

        Integer waitShardCount = jobInstanceShardDao.getJobInstanceStatusShardCount(instanceId, JobInstanceShardStatus.NEW);
        runningInstance.setWaitShardCount(waitShardCount);

        Integer runningShardCount = jobInstanceShardDao.getJobInstanceStatusShardCount(instanceId, JobInstanceShardStatus.RUNNING);
        runningInstance.setRunningShardCount(runningShardCount);

        Integer successShardCount = jobInstanceShardDao.getJobInstanceStatusShardCount(instanceId, JobInstanceShardStatus.SUCCESS);
        runningInstance.setSuccessShardCount(successShardCount);

        Integer failedShardCount = jobInstanceShardDao.getJobInstanceStatusShardCount(instanceId, JobInstanceShardStatus.FAILED);
        runningInstance.setFailedShardCount(failedShardCount);

        runningInstance.setFinishPercent((successShardCount + failedShardCount) * 100 / totalShardCount);

        return runningInstance;
    }


    private Response<Boolean> updateJobStatus(Long jobId, JobStatus status) {
        try {
            Job job = jobDao.findById(jobId);
            if (job == null){
                Logs.warn("The job(id={}) doesn't exist when disable.", jobId);
                return Response.ok(true);
            }

            if (Objects.equal(status.value(), job.getStatus())){
                return Response.ok(true);
            }

            job.setStatus(status.value());

            return Response.ok(jobDao.save(job));
        } catch (Exception e){
            Logs.error("failed to update the job(jobId={}) to status({}), cause: {}",
                    jobId, status, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.update.status.failed");
        }
    }

    @Override
    public Response<Boolean> createJobInstanceAndShards(JobInstance instance, JobConfig config) {
        try {

            // create job instance
            instance.setMaxShardPullCount(config.getMaxShardPullCount());
            instance.setJobParam(config.getParam());
            instance.setTotalShardCount(config.getShardCount());
            if(!jobInstanceManager.create(instance)){
                Logs.error("failed to create job instance({}).", instance);
                return Response.ok(false);
            }

            // create shards
            List<Long> shardIds = createJobInstanceShards(instance, config);

            // create the shards counter
            if(!jobInstanceShardDao.createNewShardsSet(instance.getId(), shardIds)){
                Logs.error("failed to create shards counter(jobInstanceId={}).", instance.getId());
                return Response.ok(false);
            }

            return Response.ok(true);

        } catch (ShardOperateException e){
            Logs.error("failed to create job instance shard, cause: {}", Throwables.getStackTraceAsString(e));
            return Response.ok(false);
        } catch (Exception e){
            Logs.error("failed to create job instance and shards(instance={}, config={}), cause: {}",
                    instance, config, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.shard.create.failed");
        }
    }

    private List<Long> createJobInstanceShards(JobInstance instance, JobConfig config) {

        JobInstanceShard shard;
        List<Long> shardIds = Lists.newArrayListWithExpectedSize(config.getShardCount());

        String[] shardParams = null;
        if (!Strings.isNullOrEmpty(config.getShardParams())){
            shardParams = config.getShardParams().split(Constants.JOB_SHARD_PARAMS_DELIMITER);
        }

        for (int i=0; i<config.getShardCount(); i++){
            shard = new JobInstanceShard();
            shard.setInstanceId(instance.getId());

            shard.setStatus(JobInstanceShardStatus.NEW.value());
            shard.setPullCount(0);
            shard.setItem(i);
            if (shardParams != null && shardParams.length > i){
                shard.setParam(shardParams[shard.getItem()].split(Constants.JOB_SHARD_PARAMS_KV_DELIMITER)[1]);
            }

            if (!jobInstanceShardManager.save(shard)){
                Logs.error("failed to create job instance shard(instance={}, config={}).", instance, config);
                throw new ShardOperateException(ShardOperateRespCode.SHARD_CREATE_FAILED);
            }
            shardIds.add(shard.getId());
        }

        return shardIds;
    }

    @Override
    public Response<PullShard> pullJobInstanceShard(Long jobInstanceId, String client) {
        try {

            // check job instance status
            JobInstance instance = checkJobInstanceStatus(jobInstanceId);

            // pull the shard and update the shard
            Integer maxPullShardCount = instance.getMaxShardPullCount();
            JobInstanceShard shard = jobInstanceShardManager.pullShard(jobInstanceId, client, maxPullShardCount);

            PullShard pullShard = buildPullShard(shard, instance);

            return Response.ok(pullShard);

        } catch (ShardOperateException e){
            return Response.notOk(Response.BUSINESS_ERR, e.getCode().value());
        } catch (Exception e){
            Logs.error("failed to pull job instance shard(instanceId={}), cause: {}",
                    jobInstanceId, Throwables.getStackTraceAsString(e));
            return Response.notOk(Response.BUSINESS_ERR, ShardOperateRespCode.SHARD_PULL_FAILED.value());
        }
    }

    private PullShard buildPullShard(JobInstanceShard shard, JobInstance instance) {

        PullShard pullShard = new PullShard();
        pullShard.setId(shard.getId());
        pullShard.setItem(shard.getItem());
        pullShard.setParam(shard.getParam());
        pullShard.setJobParam(instance.getJobParam());
        pullShard.setTotalShardCount(instance.getTotalShardCount());

        return pullShard;
    }

    @Override
    public Response<Boolean> returnJobInstanceShard(Long jobInstanceId, Long shardId, String client) {
        try {

            // check job instance status
            checkJobInstanceStatus(jobInstanceId);

            // push the shard back to shards set
            Boolean success = jobInstanceShardManager.returnShard(jobInstanceId, shardId, client);

            return Response.ok(success);
        } catch (ShardOperateException e){
            return Response.notOk(Response.BUSINESS_ERR, e.getCode().value());
        } catch (Exception e){
            Logs.error("failed to push job instance shard(instanceId={}, shardId={}, client={}), cause: {}",
                    jobInstanceId, shardId, client, Throwables.getStackTraceAsString(e));
            return Response.notOk(Response.BUSINESS_ERR, ShardOperateRespCode.SHARD_RETURN_FAILED.value());
        }
    }

    private JobInstance checkJobInstanceStatus(Long jobInstanceId) {
        JobInstance instance = jobInstanceDao.findById(jobInstanceId);
        if (instance == null){
            Logs.warn("The job instance(id={}) isn't exist when pull shard.", jobInstanceId);
            throw new ShardOperateException(ShardOperateRespCode.INSTANCE_NOT_EXIST);
        }

        JobInstanceStatus instanceStatus = JobInstanceStatus.from(instance.getStatus());
        if (instanceStatus == JobInstanceStatus.SUCCESS
                || instanceStatus == JobInstanceStatus.FAILED){
            throw new ShardOperateException(ShardOperateRespCode.INSTANCE_FINISH);
        }

        return instance;
    }

    @Override
    public Response<Boolean> finishJobInstanceShard(ShardFinishDto shardFinishDto) {
        JobInstance instance = null;
        Boolean finished = Boolean.FALSE;
        try {
            // check job instance status
            instance = checkJobInstanceStatus(shardFinishDto.getInstanceId());

            // finish the job shard
            finished = jobInstanceShardManager.finishShard(shardFinishDto);
            if (!finished){
                return Response.notOk(Response.BUSINESS_ERR, ShardOperateRespCode.SHARD_FINISH_FAILED);
            }

            return Response.ok(Boolean.TRUE);
        } catch (ShardOperateException e){
            return Response.notOk(Response.BUSINESS_ERR, e.getCode().value());
        } catch (Exception e){
            Logs.error("failed to finish job instance shard(shardFinishDto={}), cause: {}",
                    shardFinishDto, Throwables.getStackTraceAsString(e));
            return Response.notOk(Response.BUSINESS_ERR, ShardOperateRespCode.SHARD_FINISH_FAILED.value());
        } finally {
            // check whether the job instance has finished or not
            if (instance != null && finished){
                jobSupport.checkJobInstanceFinish(shardFinishDto);
            }
        }
    }

    @Override
    public Response<Boolean> returnJobInstanceShardsOfClient(String client) {
        try {

            List<Long> shardIds = jobInstanceShardDao.getClientRunningShards(client);

            if (!CollectionUtil.isNullOrEmpty(shardIds)){
                JobInstanceShard shard;
                for (Long shardId : shardIds){
                    shard = jobInstanceShardDao.findById(shardId);
                    if (shard != null &&
                            Objects.equal(JobInstanceShardStatus.RUNNING.value(), shard.getStatus())){
                        // push the shard back to shards set
                        if(!jobInstanceShardManager.returnShard(shard.getInstanceId(), shardId, client)){
                            Logs.warn("failed to push the shard({}) back by client({}).", shard, client);
                        }
                    }
                }
            }

            return Response.ok(true);

        } catch (ShardOperateException e){
            return Response.notOk(Response.BUSINESS_ERR, e.getCode().value());
        } catch (Exception e){
            Logs.error("failed to return job instance shards by client(client={}), cause: {}",
                    client, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.shard.return.failed");
        }
    }

    @Override
    public Response<JobInstanceShard> findJobInstanceShardById(Long shardId) {
        try {
            return Response.ok(jobInstanceShardDao.findById(shardId));
        } catch (Exception e){
            Logs.error("failed to find job instance shard(id={}), cause: {}",
                    shardId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.instance.shard.find.failed");
        }
    }

    private App findAppById(Long appId) {
        Response<App> findResp = appService.findById(appId);
        if (!findResp.isSuccess()){
            throw new RuntimeException("Failed to find app, id = " + appId);
        }

        App app = findResp.getData();
        if (app == null){
            throw new RuntimeException("The app isn't exist, id = " + appId);
        }

        return app;
    }
}
