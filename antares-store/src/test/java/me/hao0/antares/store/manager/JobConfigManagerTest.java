package me.hao0.antares.store.manager;

import me.hao0.antares.common.model.JobConfig;
import me.hao0.antares.store.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobConfigManagerTest extends BaseTest {

    @Autowired
    private JobConfigManager jobConfigManager;

    @Test
    public void testSave(){
        JobConfig config = new JobConfig();
        config.setJobId(1L);
        config.setMisfire(Boolean.TRUE);
        config.setShardCount(4);
        config.setShardParams("0=0;1=1;2=2;3=3");
        assertTrue(jobConfigManager.save(config));
    }

    @Test
    public void testDelete(){
        assertTrue(jobConfigManager.delete(1L));
    }
}
