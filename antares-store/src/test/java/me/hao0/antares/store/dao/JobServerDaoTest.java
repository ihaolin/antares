package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.JobServer;
import me.hao0.antares.store.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobServerDaoTest extends BaseTest {

    @Autowired
    private JobServerDao jobServerDao;

    @Test
    public void testBind(){
        jobServerDao.bind(new JobServer(1L, "127.0.0.1:22122"));
    }

    @Test
    public void testFindJobsByServer(){
        assertEquals(11, jobServerDao.findJobsByServer("127.0.0.1:12345").size());
    }

    @Test
    public void testFindServerByJobId(){
        assertNotNull(jobServerDao.findServerByJobId(2L));
        assertNull(jobServerDao.findServerByJobId(404L));
    }

    @Test
    public void testUnbindJob(){
        assertTrue(jobServerDao.unbindJob(3L));
    }

    @Test
    public void testUnbindServer(){
        assertTrue(jobServerDao.unbindJobsOfServer("127.0.0.1:12345"));
    }
}
