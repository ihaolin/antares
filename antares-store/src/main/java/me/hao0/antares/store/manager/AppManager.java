package me.hao0.antares.store.manager;

import me.hao0.antares.common.model.App;
import me.hao0.antares.store.dao.AppDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class AppManager {

    @Autowired
    private AppDao appDao;

    /**
     * Delete the app
     * @param appId the app id
     */
    public Boolean delete(Long appId){

        App app = appDao.findById(appId);
        if (app == null){
            return Boolean.TRUE;
        }

        // delete the app index
        if(appDao.unIndex(app)){
            // delete the app
            return appDao.delete(app.getId());
        }

        return Boolean.FALSE;
    }

    public Boolean save(App app) {
        // save the app
        if (appDao.save(app)){
            // index app if necessary
            return appDao.index(app);
        }
        return Boolean.FALSE;
    }
}
