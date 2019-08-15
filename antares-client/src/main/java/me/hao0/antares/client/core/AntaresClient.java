package me.hao0.antares.client.core;

import me.hao0.antares.client.job.Job;
import me.hao0.antares.client.job.execute.JobExecutor;
import me.hao0.antares.client.job.JobManager;
import me.hao0.antares.common.model.App;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.zk.ZkClient;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface AntaresClient extends Lifecycle {

    /**
     * Get the client version
     * @return the client version
     */
    String getClientVersion();

    /**
     * Get the app name
     * @return the app name
     */
    String getAppName();

    /**
     * Get the app key
     * @return the app key
     */
    String getAppKey();

    /**
     * Get the zk namespace
     * @return the zk namespace
     */
    String getZkNamespace();

    /**
     * Get the zk server list
     * @return the zk server list
     */
    String getZkServers();

    /**
     * Get the thread count for executing jobs
     * @return the thread count
     */
    Integer getExecutorThreadCount();

    /**
     * Get the zk client
     * @return the zk client
     */
    ZkClient getZk();

    /**
     * Get the job manager
     * @return the job manager
     */
    JobManager getJobManager();

    /**
     * Get the job executor
     * @return the job executor
     */
    JobExecutor getJobExecutor();

    void setJobExecutor(JobExecutor jobExecutor);

    /**
     * Get the http agent
     * @return the http agent
     */
    AntaresHttpAgent getHttp();

    /**
     * Get the current http servers
     * @return the current http servers
     */
    List<String> getHttpServers();

    /**
     * Add a http server
     * @param httpServer the http server
     */
    void addHttpServer(String httpServer);

    /**
     * Remove a http server
     * @param httpServer the http server
     */
    void removeHttpServer(String httpServer);

    /**
     * Register the job
     * @param job the job
     */
    void registerJob(Job job);

    /**
     * Register client app
     */
    void registerApp();
}
