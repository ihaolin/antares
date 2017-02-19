package me.hao0.antares.store;

import me.hao0.antares.common.model.*;
import me.hao0.antares.common.model.enums.JobInstanceStatus;
import me.hao0.antares.common.model.enums.JobStatus;
import me.hao0.antares.common.model.enums.JobType;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Bootstrap.class)
public class BaseTest {

    protected Job mockJob() {

        Job job = new Job();

        job.setAppId(1L);
        job.setType(JobType.DEFAULT.value());
        job.setCron("* 0/5 * * * ?");
        job.setStatus(JobStatus.ENABLE.value());
        job.setClazz("me.hao0.antares.job.DemoSimpleJob");

        return job;
    }

    protected JobConfig mockJobConfig(Integer shardingCount, String shardingParam){
        JobConfig config = new JobConfig();

        config.setShardCount(shardingCount);
        config.setShardParams(shardingParam);
        config.setMisfire(Boolean.TRUE);

        return config;
    }

    protected JobInstance mockJobInstance(Long jobId){
        JobInstance instance = new JobInstance();
        instance.setJobId(jobId);
        instance.setServer("127.0.0.1:12345");
        instance.setStartTime(new Date());
        instance.setStatus(JobInstanceStatus.NEW.value());
        return instance;
    }
}
