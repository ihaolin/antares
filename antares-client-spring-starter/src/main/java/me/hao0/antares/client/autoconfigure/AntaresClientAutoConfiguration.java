package me.hao0.antares.client.autoconfigure;

import me.hao0.antares.client.core.SimpleAntaresClient;
import me.hao0.antares.client.job.DefaultJob;
import me.hao0.antares.client.job.script.ScriptJob;
import me.hao0.antares.common.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

/**
 * The Antares Client Auto Configuration Entry
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
@Configuration
@EnableConfigurationProperties(AntaresClientProperties.class)
public class AntaresClientAutoConfiguration {

    @Autowired
    private AntaresClientProperties properties;

    @Autowired
    private ApplicationContext springContext;

    @Bean(destroyMethod = "shutdown")
    public SimpleAntaresClient buildSpringClient(){

        SimpleAntaresClient client = new SimpleAntaresClient(
                                            properties.getAppName(),
                                            properties.getAppSecret(),
                                            properties.getZkServers(),
                                            properties.getZkNamespace());

        client.setExecutorThreadCount(properties.getExecutorThreadCount());

        client.start();

        registerJobs(client);

        return client;
    }

    private void registerJobs(final SimpleAntaresClient client) {

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
}
