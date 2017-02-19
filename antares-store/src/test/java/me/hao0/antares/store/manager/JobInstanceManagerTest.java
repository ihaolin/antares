package me.hao0.antares.store.manager;

import me.hao0.antares.common.model.JobInstance;
import me.hao0.antares.common.model.enums.JobInstanceStatus;
import me.hao0.antares.store.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobInstanceManagerTest extends BaseTest {

    @Autowired
    private JobInstanceManager jobInstanceManager;

    @Test
    public void testCreate(){

        JobInstance instance = new JobInstance();
        instance.setJobId(1L);
        instance.setStatus(JobInstanceStatus.NEW.value());
        instance.setStartTime(new Date());
        instance.setServer("127.0.0.1:1122");

        assertTrue(jobInstanceManager.create(instance));
    }
}
