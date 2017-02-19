package me.hao0.antares.store.manager;

import me.hao0.antares.common.model.Job;
import me.hao0.antares.common.model.enums.JobStatus;
import me.hao0.antares.common.model.enums.JobType;
import me.hao0.antares.store.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobManagerTest extends BaseTest {

    @Autowired
    private JobManager jobManager;

    @Test
    public void testSave(){
        Job job = new Job();
        job.setAppId(1L);
        job.setClazz("me.hao0.antares.client.job.HelloJob");
        job.setCron("0/30 * * * * ?");
        job.setStatus(JobStatus.ENABLE.value());
        job.setType(JobType.DEFAULT.value());
        assertTrue(jobManager.save(job));
    }

    @Test
    public void testDelete(){

    }
}
