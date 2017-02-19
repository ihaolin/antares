package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.App;
import me.hao0.antares.store.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */

public class AppDaoTest extends BaseTest {

    @Autowired
    private AppDao appDao;

    @Test
    public void testSave(){
        App app = new App();
        app.setAppName("test_app");
        app.setAppKey("123456");
        app.setAppDesc("测试应用");

        assertTrue(appDao.save(app));
    }

    @Test
    public void testFindById(){
        App app = appDao.findById(3L);
        assertNotNull(app);
        System.out.println(app);

        app = appDao.findById(404L);
        assertNull(app);
    }

    @Test
    public void testDelete(){
        assertTrue(appDao.delete(3L));
        assertTrue(appDao.delete(404L));
    }

    @Test
    public void testFindByName(){
        String appName = "not_found";
        assertNull(appDao.findByName(appName));

        appName = "app_test1";
        assertNotNull(appDao.findByName(appName));
    }
}
