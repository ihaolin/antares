package me.hao0.antares.store.manager;

import me.hao0.antares.common.model.App;
import me.hao0.antares.store.BaseTest;
import me.hao0.antares.store.dao.AppDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AppManagerTest extends BaseTest {

    @Autowired
    private AppManager appManager;

    @Autowired
    private AppDao appDao;

    @Test
    public void testSave(){
        App app = new App();
        app.setAppName("test_app");
        app.setAppKey("123456");
        app.setAppDesc("测试应用");
        assertTrue(appManager.save(app));
        assertNotNull(appDao.findByName(app.getAppName()));
    }

    @Test
    public void testDelete(){
        assertTrue(appManager.delete(4L));
        assertTrue(appManager.delete(404L));
    }
}
