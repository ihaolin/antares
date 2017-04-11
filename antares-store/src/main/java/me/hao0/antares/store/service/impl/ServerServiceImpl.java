package me.hao0.antares.store.service.impl;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import me.hao0.antares.common.balance.LoadBalance;
import me.hao0.antares.common.balance.RandomLoadBalance;
import me.hao0.antares.common.dto.JobDetail;
import me.hao0.antares.common.exception.JobFindException;
import me.hao0.antares.common.exception.JobStateTransferInvalidException;
import me.hao0.antares.common.http.Http;
import me.hao0.antares.common.http.HttpMethod;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.enums.JobState;
import me.hao0.antares.common.retry.RetryException;
import me.hao0.antares.common.retry.Retryer;
import me.hao0.antares.common.retry.Retryers;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.common.util.Constants;
import me.hao0.antares.common.util.Systems;
import me.hao0.antares.store.dao.JobServerDao;
import me.hao0.antares.store.exception.JobNotExistException;
import me.hao0.antares.store.exception.JobServerException;
import me.hao0.antares.store.service.ClusterService;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.store.service.ServerService;
import me.hao0.antares.store.support.JobSupport;
import me.hao0.antares.common.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import static me.hao0.antares.store.util.ServerUris.*;

/**
 * The service for calling server
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    private JobServerDao jobServerDao;

    @Autowired
    private JobService jobService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private JobSupport jobSupport;

    private final Retryer<Boolean> serverRetryer = Retryers.get().newRetryer(Predicates.<Boolean>alwaysFalse(), 3);

    private final ExecutorService executor =
            Executors.newFixedThreadPool(Systems.cpuNum() + 1,
            new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("SERVER-SERVICE-WORKER-" + index.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                }
            });

    /**
     * Use random balance simply
     */
    private LoadBalance<String> balancer = new RandomLoadBalance<>();

    @Override
    public Response<Boolean> scheduleJob(Long jobId) {
        try {

            // get current server list
            Response<List<String>> listResp = clusterService.listSimpleServers();
            if (!listResp.isSuccess()){
                return Response.notOk(listResp.getErr());
            }

            List<String> servers = listResp.getData();
            if (CollectionUtil.isNullOrEmpty(servers)){
                // no available server, don't need schedule
                Logs.warn("There are no available servers when schedule job(id={}).", jobId);
                return Response.notOk("server.no.available");
            }

            String targetServer = balancer.balance(servers);

            return Response.ok(doScheduleJob(jobId, targetServer));
        } catch (Exception e) {
            Logs.error("failed to schedule job(jobId={})", jobId);
            return Response.notOk("job.schedule.failed");
        }
    }

    @Override
    public Response<Boolean> scheduleJobIfPossible(Long jobId) {
        try {

            String scheduleServer = jobServerDao.findServerByJobId(jobId);
            if (!Strings.isNullOrEmpty(scheduleServer)){
                // re scheduling
                reloadJob(jobId);
                return Response.ok(true);
            }

            // get current server list
            Response<List<String>> listResp = clusterService.listSimpleServers();
            if (!listResp.isSuccess()){
                Logs.error("failed to list servers when schedule job(id={}) possible, cause: {}, but ignore",
                        jobId, listResp.getErr());
                return Response.ok(true);
            }

            List<String> servers = listResp.getData();
            if (CollectionUtil.isNullOrEmpty(servers)){
                // no available server, don't need schedule
                Logs.warn("There are no available server when schedule job(id={}) possible, but ignore.", jobId);
                return Response.ok(true);
            }

            return scheduleJob(jobId);

        } catch (Exception e) {
            Logs.error("failed to schedule job if possible(jobId={})", jobId);
            return Response.notOk("job.schedule.failed");
        }
    }

    /**
     * Schedule the job to one of the servers
     * @param jobId the job id
     * @param servers the alive servers
     */
    public Response<Boolean> scheduleJob(Long jobId, List<String> servers){
        try {
            String targetServer = balancer.balance(servers);
            return Response.ok(doScheduleJob(jobId, targetServer));
        } catch (Exception e) {
            Logs.error("failed to schedule job(jobId={}, servers={})", jobId, servers);
            return Response.notOk("job.schedule.failed");
        }
    }

    /**
     * Schedule the jobs to the server
     * @param jobIds the job ids
     * @param servers the alive servers
     */
    public Response<Boolean> scheduleJobs(List<Long> jobIds, final List<String> servers){

        try {
            final CountDownLatch latch = new CountDownLatch(jobIds.size());
            for(final Long jobId : jobIds){
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        scheduleJob(jobId, servers);
                        latch.countDown();
                    }
                });
            }
            latch.await();
            return Response.ok(true);
        } catch (InterruptedException e) {
            Logs.error("failed to count down latch await, cause: {}", Throwables.getStackTraceAsString(e));
            return Response.notOk("job.schedule.failed");
        }
    }

    private Boolean doScheduleJob(Long jobId, String targetServer) {
        String uri = JOB_SCHEDULE + "/" + jobId;
        try {
            return serverRetryer.call(new RetryableServerTask(targetServer, uri));
        } catch (Exception e) {
            Logs.info("failed to schedule job(jobId={}, server={}), cause: {}",
                    jobId, targetServer, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }
    }

    @Override
    public Response<Boolean> triggerJob(Long jobId) {
        try {
            checkJobState(jobId, JobState.WAITING, JobState.RUNNING);
            return Response.ok(doOperateJob(jobId, JOB_TRIGGER + "/" + jobId));
        } catch (JobFindException e){
            return Response.notOk("job.find.failed");
        } catch (JobStateTransferInvalidException e){
            return Response.notOk("job.state.operate.invalid");
        } catch (JobNotExistException e){
            return Response.notOk("job.not.exist");
        } catch (JobServerException e){
            return Response.notOk(e.getMessage());
        } catch (Exception e) {
            Logs.info("failed to trigger job(jobId={}), cause: {}", jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.trigger.failed");
        }
    }

    @Override
    public Response<Boolean> notifyJob(Long jobId) {
        try {
            checkJobState(jobId, JobState.WAITING, JobState.RUNNING);
            return Response.ok(doOperateJob(jobId, JOB_NOTIFY + "/" + jobId));
        } catch (JobFindException e){
            return Response.notOk("job.find.failed");
        } catch (JobStateTransferInvalidException e){
            return Response.notOk("job.state.operate.invalid");
        } catch (JobNotExistException e){
            return Response.notOk("job.not.exist");
        } catch (JobServerException e){
            return Response.notOk(e.getMessage());
        } catch (Exception e) {
            Logs.info("failed to notify job(jobId={}), cause: {}", jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.notify.failed");
        }
    }

    @Override
    public Response<Boolean> pauseJob(Long jobId) {
        try {
            checkJobState(jobId, null, JobState.PAUSED);
            return Response.ok(doOperateJob(jobId, JOB_PAUSE + "/" + jobId));
        } catch (JobFindException e){
            return Response.notOk("job.find.failed");
        } catch (JobStateTransferInvalidException e){
            return Response.notOk("job.state.operate.invalid");
        } catch (JobNotExistException e){
            return Response.notOk("job.not.exist");
        } catch (JobServerException e){
            return Response.notOk(e.getMessage());
        } catch (Exception e) {
            Logs.info("failed to pause job(jobId={}), cause: {}", jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.pause.failed");
        }
    }

    @Override
    public Response<Boolean> resumeJob(Long jobId) {
        try {
            checkJobState(jobId, JobState.PAUSED, JobState.WAITING);
            return Response.ok(doOperateJob(jobId, JOB_RESUME + "/" + jobId));
        } catch (JobServerException e){
            return Response.notOk(e.getMessage());
        } catch (Exception e) {
            Logs.info("failed to resume job(jobId={}), cause: {}", jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.resume.failed");
        }
    }

    @Override
    public Response<Boolean> removeJob(Long jobId) {
        try {
            checkJobState(jobId, null, JobState.STOPPED);
            doOperateJob(jobId, JOB_REMOVE + "/" + jobId);
            return Response.ok(Boolean.TRUE);
        } catch (JobServerException e){
            return Response.ok(true);
        } catch (Exception e) {
            Logs.info("failed to remove job(jobId={}), cause: {}", jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.remove.failed");
        }
    }

    @Override
    public Response<Boolean> reloadJob(Long jobId) {
        try {
            return Response.ok(doOperateJob(jobId, JOB_RELOAD + "/" + jobId));
        } catch (JobServerException e){
            return Response.notOk(e.getMessage());
        } catch (Exception e) {
            Logs.info("failed to reload job(jobId={}), cause: {}", jobId, Throwables.getStackTraceAsString(e));
            return Response.notOk("job.reload.failed");
        }
    }

    private Boolean doOperateJob(Long jobId, String uri) throws ExecutionException, RetryException {
        String server = getScheduleServer(jobId);
        if (Strings.isNullOrEmpty(server)){
            Logs.warn("The job({}) isn't scheduling when call {}.", jobId, uri);
            return Boolean.FALSE;
        }
        return serverRetryer.call(new RetryableServerTask(server, uri));
    }

    /**
     * Get the job's current schedule server
     * @param jobId the job id
     * @return the job schedule server
     */
    private String getScheduleServer(Long jobId){

        Response<String> serverResp = jobService.findServerOfJob(jobId);
        if (!serverResp.isSuccess()){
            throw new JobServerException(serverResp.getErr().toString());
        }

        String server = serverResp.getData();
        if (Strings.isNullOrEmpty(server)){
            throw new JobServerException("job.not.scheduled.by.server");
        }

        return server;
    }

    private void checkJobState(Long jobId, JobState expectState, JobState targetState) {

        Response<JobDetail> jobResp = jobService.findJobDetailById(jobId);
        if(!jobResp.isSuccess()){
            throw new JobFindException();
        }

        JobDetail jobDetail = jobResp.getData();
        if (jobDetail == null){
            Logs.warn("The job(id={}) isn't exist.", jobId);
            throw new JobNotExistException(jobId);
        }

        String appName = jobDetail.getApp().getAppName();
        String jobClass = jobDetail.getJob().getClazz();

        jobSupport.checkJobStateOperate(appName, jobClass, expectState, targetState);
    }

    /**
     * The task for invoking the server
     */
    private class RetryableServerTask implements Callable<Boolean> {

        private String server;

        private String uri;

        private Map<String, String> headers;

        private Map<String, Object> params;

        public RetryableServerTask(String server, String uri) {
            this.server = server;
            this.uri = uri;
        }

        @Override
        public Boolean call() throws Exception {
            return doRequest(server, uri, HttpMethod.POST, headers, params, 0);
        }
    }

    private Boolean doRequest(String server, String uri, HttpMethod method,
                              Map<String, String> headers, Map<String, Object> params, int readTimeout){

        try {
            String reqUri = Constants.HTTP_PREFIX + server + SERVERS + uri;

            Http http;
            if (method == HttpMethod.GET){
                http = Http.get(reqUri);
            } else {
                http = Http.post(reqUri);
            }

            if (readTimeout > 0){
                http.readTimeout(readTimeout);
            }

            if (headers != null){
                http.headers(headers);
            }

            if (params != null){
                http.params(params);
            }

            String resp = http.request();

            return "true".equals(resp);

        } catch (HttpRequest.HttpRequestException e){
            Logs.warn("The server isn't available now.", server);
            return Boolean.TRUE;
        } catch (Exception e){
            Logs.error("failed to request server(server={}, uri={}, params={}), cause: {}",
                    server, uri, params, Throwables.getStackTraceAsString(e));
            return Boolean.FALSE;
        }
    }
}
