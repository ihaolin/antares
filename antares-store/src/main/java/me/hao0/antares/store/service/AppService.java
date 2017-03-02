package me.hao0.antares.store.service;

import me.hao0.antares.common.model.App;
import me.hao0.antares.store.util.Page;
import me.hao0.antares.store.util.Response;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface AppService {

    /**
     * Add an application
     * @param app the app
     * @return the app id
     */
    Response<Long> save(App app);

    /**
     * Find an application by name
     * @param name the app name
     * @return the app
     */
    Response<App> findByName(String name);

    /**
     * Find an application by id
     * @param id the app id
     * @return the app
     */
    Response<App> findById(Long id);

    /**
     * List all applications
     * @param appName the app name(full match)
     * @param pageNo page no
     * @param pageSize page size
     * @return all applications
     */
    Response<Page<App>> pagingApp(String appName, Integer pageNo, Integer pageSize);

    /**
     * Delete the app
     * @param appName the app name
     * @return return true if delete successfully, or false
     */
    Response<Boolean> delete(String appName);
}
