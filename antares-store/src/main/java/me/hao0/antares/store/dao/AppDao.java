package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.App;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface AppDao extends BaseDao<App> {

    /**
     * Index the app's name for findByName
     * @param app app
     * @return return true if index successfully, or false
     */
    Boolean index(App app);

    /**
     * Unindex the app's name
     * @param app app
     * @return return true if unindex successfully, or false
     */
    Boolean unIndex(App app);

    /**
     * Find app by name
     * @param name the app's name
     * @return the app
     */
    App findByName(String name);
}
