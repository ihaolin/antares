package me.hao0.antares.store.dao;

import me.hao0.antares.store.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

/**
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
public class JobAssignDaoTest extends BaseTest {

    @Autowired
    private JobAssignDao jobAssignDao;

    @Test
    public void testAddAssign(){
        assertTrue(jobAssignDao.addAssign(2L, "192.168.111.111"));
    }

    @Test
    public void testBatchAddAssign(){

        for (int i=0; i<100; i++){
            assertTrue(jobAssignDao.addAssign(2L, "192.168.111.1" + i));
        }
    }

    @Test
    public void testIsAssign(){

        assertTrue(jobAssignDao.isAssigned(2L, "192.168.111.111"));
        assertFalse(jobAssignDao.isAssigned(2L, "192.168.111.222"));
        assertFalse(jobAssignDao.isAssigned(1L, "192.168.111.111"));
    }

    @Test
    public void testRemoveAssign(){

        assertTrue(jobAssignDao.removeAssign(2L, "192.168.111.111"));
        assertFalse(jobAssignDao.removeAssign(1L, "192.168.111.111"));
    }
}
