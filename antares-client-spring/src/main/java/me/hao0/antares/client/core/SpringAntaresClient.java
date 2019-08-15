package me.hao0.antares.client.core;

import me.hao0.antares.client.job.DefaultJob;
import me.hao0.antares.client.job.Job;
import me.hao0.antares.client.job.script.ScriptJob;
import me.hao0.antares.common.util.CollectionUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SpringAntaresClient implements InitializingBean, DisposableBean {

    @Autowired
    private ApplicationContext springContext;

    private final SimpleAntaresClient client;

    public SpringAntaresClient(String appName, String appSecret, String zkServers) {
        this(appName, appSecret, zkServers, null);
    }

    public SpringAntaresClient(String appName, String appSecret, String zkServers, String zkNamespace){
        client = new SimpleAntaresClient(appName, appSecret, zkServers, zkNamespace);
    }

    public void setExecutorThreadCount(Integer executorThreadCount) {
        client.setExecutorThreadCount(executorThreadCount);
    }

    @Override
    public void afterPropertiesSet() {

        // start the client
        client.start();

        // register the jobs
        registerJobs();

        // register client app
        client.registerApp();
    }

    private void registerJobs() {

        // register default jobs
        Map<String, DefaultJob> defaultJobs = springContext.getBeansOfType(DefaultJob.class);
        if (!CollectionUtil.isNullOrEmpty(defaultJobs)){
            for (DefaultJob defaultJob : defaultJobs.values()){
                client.registerJob(defaultJob);
            }
        }

        // register script jobs
        Map<String, ScriptJob> scriptJobs = springContext.getBeansOfType(ScriptJob.class);
        if (!CollectionUtil.isNullOrEmpty(scriptJobs)){
            for (ScriptJob scriptJob : scriptJobs.values()){
                client.registerJob(scriptJob);
            }
        }
    }

    @Override
    public void destroy() {
        client.shutdown();
    }
}
