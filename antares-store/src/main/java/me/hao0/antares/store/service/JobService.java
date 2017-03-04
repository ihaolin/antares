package me.hao0.antares.store.service;

import me.hao0.antares.common.dto.DependenceJob;
import me.hao0.antares.common.dto.JobControl;
import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.dto.JobEditDto;
import me.hao0.antares.common.dto.JobInstanceDetail;
import me.hao0.antares.common.dto.JobInstanceDto;
import me.hao0.antares.common.dto.JobInstanceShardDto;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.model.Job;
import me.hao0.antares.common.model.JobConfig;
import me.hao0.antares.common.model.JobDependence;
import me.hao0.antares.common.model.JobInstance;
import me.hao0.antares.common.model.JobInstanceShard;
import me.hao0.antares.store.util.Page;
import me.hao0.antares.store.util.Response;
import java.util.List;

/**
 * The job service
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface JobService {

    /**
     * Save the job dto
     * @param editing the job edit dto
     * @return return the job id
     */
    Response<Long> saveJob(JobEditDto editing);

    /**
     * Save the job detail
     * @param jobDetail the job detail
     * @return return the job id
     */
    Response<Long> saveJobDetail(JobDetail jobDetail);

    /**
     * Delete the job physically
     * @param jobId the job id
     * @return return true if delete successfully, or false
     */
    Response<Boolean> deleteJob(Long jobId);

    /**
     * Find the job
     * @param jobId the job id
     * @return the job
     */
    Response<Job> findJobById(Long jobId);

    /**
     * Find the job detail
     * @param jobId the job id
     * @return the job detail
     */
    Response<JobDetail> findJobDetailById(Long jobId);

    /**
     * Paging the job
     * @param appId the app id
     * @param jobClass the job class full name
     * @param pageNo the page number
     * @param pageSize the page size
     * @return the job page data
     */
    Response<Page<Job>> pagingJob(Long appId, String jobClass, Integer pageNo, Integer pageSize);

    /**
     * Paging the job control
     * @param appId the app id
     * @param jobClass the job class
     * @param pageNo the page number
     * @param pageSize the page size
     * @return the job control page data
     */
    Response<Page<JobControl>> pagingJobControl(Long appId, String jobClass, Integer pageNo, Integer pageSize);

    /**
     * Save the job instance
     * @param instance the job instance
     * @return return true if save successfully, or false
     */
    Response<Boolean> createJobInstance(JobInstance instance);

    /**
     * The job instance is failed
     * @param jobInstanceId the job instance id
     * @param cause the failed cause
     * @return return true if operate successfully, or false
     */
    Response<Boolean> failedJobInstance(Long jobInstanceId, String cause);

    /**
     * Find the job instance
     * @param instanceId the job instance id
     * @return the job instance
     */
    Response<JobInstance> findJobInstanceById(Long instanceId);

    /**
     * Paging the job instance
     * @param appId the app id
     * @param jobClass the job class
     * @param pageNo the page no
     * @param pageSize the page size
     * @return job instance page data
     */
    Response<Page<JobInstanceDto>> pagingJobInstance(Long appId, String jobClass, Integer pageNo, Integer pageSize);

    /**
     * Paging the job instance progress
     * @param jobinstanceId the job instance id
     * @param pageNo the page no
     * @param pageSize the page size
     * @return the job instance progress page data
     */
    Response<Page<JobInstanceShardDto>> pagingJobInstanceShards(Long jobinstanceId, Integer pageNo, Integer pageSize);

    /**
     * Find all jobs of the server
     * @param server the server
     * @return all jobs of the server
     */
    Response<List<JobDetail>> findValidJobsByServer(String server);

    /**
     * Create the job instance and shards
     * @param instance the job instance
     * @param config the job config
     * @return return true if create successfully, or false
     */
    Response<Boolean> createJobInstanceAndShards(JobInstance instance, JobConfig config);

    /**
     * Pull the job instance's shard
     * @param jobInstanceId the job instance
     * @param client the client host
     * @return the pull shard
     */
    Response<PullShard> pullJobInstanceShard(Long jobInstanceId, String client);

    /**
     * Return back the job instance's shard back
     * @param jobInstanceId the job instance
     * @param shardId the shard id
     * @param client the client host
     * @return return true if return successfully, or false
     */
    Response<Boolean> returnJobInstanceShard(Long jobInstanceId, Long shardId, String client);

    /**
     * Finish the job instance' shard
     * @param shardFinishDto the shard finish dto
     * @return return true if finish successfully, or false
     */
    Response<Boolean> finishJobInstanceShard(ShardFinishDto shardFinishDto);

    /**
     * Return back the client's all running shards
     * @param client the client host:pid
     * @return return true if return successfully, or false
     */
    Response<Boolean> returnJobInstanceShardsOfClient(String client);

    /**
     * Find the job instance shard
     * @param shardId the shard id
     * @return the job instance shard
     */
    Response<JobInstanceShard> findJobInstanceShardById(Long shardId);

    /**
     * Find all job ids of the server
     * @param server the server
     * @return all job ids of the server
     */
    Response<List<Long>> findJobIdsByServer(String server);

    /**
     * Find all jobs of the server
     * @param server the server
     * @return all jobs of the server
     */
    Response<List<Job>> findJobsByServer(String server);

    /**
     * Remove all jobs of the server
     * @param server the server
     * @return return successfully if remove successfully, or false
     */
    Response<Boolean> removeAllJobsByServer(String server);

    /**
     * Bind the job to server for scheduling
     * @param jobId the job id
     * @param server the server host
     * @return return true if bind success, or false
     */
    Response<Boolean> bindJob2Server(Long jobId, String server);

    /**
     * Find the job's current schedule server
     * @param jobId the job id
     * @return the schedule server
     */
    Response<String> findServerOfJob(Long jobId);

    /**
     * Find the job's config
     * @param jobId the job id
     * @return the job config
     */
    Response<JobConfig> findJobConfigByJobId(Long jobId);

    /**
     * Disable the job
     * @param jobId the job id
     * @return return true if disable successfully, or false
     */
    Response<Boolean> disableJob(Long jobId);

    /**
     * Enable the job
     * @param jobId the job id
     * @return return true if enable successfully, or false
     */
    Response<Boolean> enableJob(Long jobId);

    /**
     * Monitor the job instance detail
     * @param jobId the job id
     * @return the job instance detail
     */
    Response<JobInstanceDetail> monitorJobInstanceDetail(Long jobId);

    /**
     * Find the job instance detail
     * @param jobInstanceId the job instance id
     * @return the job instance detail
     */
    Response<JobInstanceDetail> findJobInstanceDetail(Long jobInstanceId);

    /**
     * Force the finish the job
     * @param jobId the job id
     * @return return true if force to finish successfully
     */
    Response<Boolean> forceFinishJob(Long jobId);

    /**
     * Unbind the job from the server
     * @param server the server host
     * @param jobId the job id
     * @return return true if unbind successfully, or false
     */
    Response<Boolean> unbindJobServer(String server, Long jobId);

    /**
     * Add the job dependence
     * @param dependence the job dependence
     * @return return true if add successfully, or false
     */
    Response<Boolean> addJobDependence(JobDependence dependence);

    /**
     * Delete the job's next job
     * @param jobId the job id
     * @param nextJobId the next job id
     * @return return true if delete successfully, or false
     */
    Response<Boolean> deleteNextJob(Long jobId, Long nextJobId);

    /**
     * Delete the job's next jobs
     * @param jobId the job id
     * @return return true if delete successfully, or false
     */
    Response<Boolean> deleteNextJobs(Long jobId);

    /**
     * Paging the job's next jobs
     * @param jobId the job id
     * @param pageNo the page number
     * @param pageSize the page size
     * @return the job's next page jobs
     */
    Response<Page<DependenceJob>> pagingNextJobs(Long jobId, Integer pageNo, Integer pageSize);

    /**
     * Paging the job's next job ids
     * @param jobId the job id
     * @param pageNo the page number
     * @param pageSize the page size
     * @return the job's next page job ids
     */
    Response<Page<Long>> pagingNextJobIds(Long jobId, Integer pageNo, Integer pageSize);
}
