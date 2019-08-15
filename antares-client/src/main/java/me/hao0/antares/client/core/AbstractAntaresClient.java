package me.hao0.antares.client.core;

import com.google.common.base.Strings;
import me.hao0.antares.client.job.Job;
import me.hao0.antares.client.job.JobManager;
import me.hao0.antares.client.job.execute.JobExecutor;
import me.hao0.antares.client.job.execute.SimpleJobExecutor;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.common.zk.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static me.hao0.antares.common.util.ClientUris.REGISTER;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
abstract class AbstractAntaresClient extends Component implements AntaresClient {

    private static final Logger log = LoggerFactory.getLogger(AbstractAntaresClient.class);

    /**
     * The client version
     */
    private final String CLIENT_VERSION = "1.0.0";

    /**
     * The app name
     */
    private final String appName;

    /**
     * The app key
     */
    private final String appKey;

    /**
     * The zk servers
     */
    private final String zkServers;

    /**
     * The thread count for executing task
     */
    private Integer executorThreadCount = 32;

    /**
     * The http servers
     */
    private final List<String> httpServers = new CopyOnWriteArrayList<>();

    /**
     * The zk namespace
     */
    private final String zkNamespace;

    /**
     * The http agent
     */
    private final AntaresHttpAgent http = new AntaresHttpAgent(this);

    /**
     * The zk client
     */
    private final AntaresZkAgent zk;

    /**
     * The job manager
     */
    private final JobManager jobManager = new JobManager(this);

    /**
     * The job executor
     */
    private JobExecutor jobExecutor = new SimpleJobExecutor(this);

    public AbstractAntaresClient(String appName, String zkServers) {
        this(appName, null, zkServers);
    }

    public AbstractAntaresClient(String appName, String appKey, String zkServers) {
        this(appName, appKey, zkServers, null);
    }

    public AbstractAntaresClient(String appName, String appKey, String zkServers, String zkNamespace) {
        this.appName = appName;
        this.appKey = appKey;
        this.zkServers = zkServers;
        this.zkNamespace = Strings.isNullOrEmpty(zkNamespace) ? ZkPaths.DEFAULT_NS : zkNamespace;
        zk = new AntaresZkAgent(this, zkServers, this.zkNamespace);
    }

    @Override
    public String getClientVersion() {
        return CLIENT_VERSION;
    }

    @Override
    public void setJobExecutor(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getZkNamespace() {
        return zkNamespace;
    }

    public String getZkServers() {
        return zkServers;
    }

    public Integer getExecutorThreadCount() {
        return executorThreadCount;
    }

    public void setExecutorThreadCount(Integer executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }

    public ZkClient getZk() {
        return zk.client();
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public JobExecutor getJobExecutor() {
        return jobExecutor;
    }

    public AntaresHttpAgent getHttp() {
        return http;
    }

    public List<String> getHttpServers() {
        return httpServers;
    }

    public void addHttpServer(String httpServer) {
        if (!this.httpServers.contains(httpServer)) {
            this.httpServers.add(httpServer);
        }
    }

    public void removeHttpServer(String httpServer) {
        this.httpServers.remove(httpServer);
    }

    @Override
    public void doStart() {

        zk.start();

        http.start();

        jobExecutor.start();

        jobManager.start();

        afterStart();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });

        log.info("Antares client started successfully.");
    }

    /**
     * Shutdown the client
     */
    @Override
    public void doShutdown() {

        zk.shutdown();

        http.shutdown();

        jobManager.shutdown();

        jobExecutor.shutdown();

        afterShutdown();

        log.info("Antares client shutdown finished.");
    }

    @Override
    public void registerJob(Job job) {
        jobManager.registerJob(job);
    }

    @Override
    public void registerApp() {
        Map<String, Object> params = new HashMap<>();
        Set<String> jobs = jobManager.getJobs().keySet();
        params.put("appName", appName);
        params.put("appKey", appKey);
        params.put("jobs", jobs);
        http.doPost(REGISTER, null, params, 0, Map.class);
    }

    /**
     * Subclass call
     */
    protected abstract void afterStart();

    /**
     * Subclass call
     */
    protected abstract void afterShutdown();
}
