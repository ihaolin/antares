package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.JobConfig;
import me.hao0.antares.store.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */

public class JobConfigDaoTest extends BaseTest {

    @Autowired
    private JobConfigDao jobConfigDao;

    @Test
    public void testUpdateField(){

        JobConfig config = jobConfigDao.findByJobId(6L);
        assertNotNull(config);

        assertTrue(jobConfigDao.updateField(6L, "timeout", 30));
    }
}
