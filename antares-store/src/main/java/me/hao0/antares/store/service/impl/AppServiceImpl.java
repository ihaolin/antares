package me.hao0.antares.store.service.impl;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.App;
import me.hao0.antares.store.dao.AppDao;
import me.hao0.antares.store.manager.AppManager;
import me.hao0.antares.store.service.AppService;
import me.hao0.antares.store.util.Page;
import me.hao0.antares.store.util.Paging;
import me.hao0.antares.store.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppDao appDao;

    @Autowired
    private AppManager appManager;

    /**
     * App cache for 5 mins
     */
    private final LoadingCache<String, App> APP_NAME_CACHE =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<String, App>() {
                @Override
                public App load(String appName) throws Exception {
                    return appDao.findByName(appName);
                }
            });

    private final LoadingCache<Long, App> APP_ID_CACHE =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build(new CacheLoader<Long, App>() {
                @Override
                public App load(Long appId) throws Exception {
                    return appDao.findById(appId);
                }
            });

    @Override
    public Response<Long> save(App app) {
        try {

            App exist = appDao.findByName(app.getAppName());
            if (exist == null){
                exist = new App();
                exist.setAppName(app.getAppName());
            }
            exist.setAppKey(app.getAppKey());
            exist.setAppDesc(app.getAppDesc());

            appManager.save(exist);

            return Response.ok(exist.getId());
        } catch (Exception e){
            Logs.error("failed to save app({}), cause: {}", app, Throwables.getStackTraceAsString(e));
            return Response.notOk("app.save.failed");
        }
    }

    @Override
    public Response<App> findByName(String name) {
        try {
            return Response.ok(APP_NAME_CACHE.get(name));
        } catch (Exception e){
            Logs.error("failed to find app(name={}), cause: {}", name, Throwables.getStackTraceAsString(e));
            return Response.notOk("app.find.failed");
        }
    }

    @Override
    public Response<App> findById(Long id) {
        try {
            return Response.ok(APP_ID_CACHE.get(id));
        } catch (Exception e){
            Logs.error("failed to find app(id={}), cause: {}", id, Throwables.getStackTraceAsString(e));
            return Response.notOk("app.find.failed");
        }
    }

    @Override
    public Response<Page<App>> pagingApp(String appName, Integer pageNo, Integer pageSize) {
        try {

            // find an app by name
            if (!Strings.isNullOrEmpty(appName)){
                App app = appDao.findByName(appName);
                if (app == null){
                    return Response.ok(Page.<App>empty());
                }
                return Response.ok(new Page<>(1L, Lists.newArrayList(app)));
            }

            // find apps
            Long totalCount = appDao.count();
            if (totalCount <= 0L){
                return Response.ok(Page.<App>empty());
            }

            Paging paging = new Paging(pageNo, pageSize);
            List<App> apps = appDao.list(paging.getOffset(), paging.getLimit());

            return Response.ok(new Page<>(totalCount, apps));
        } catch (Exception e){
            Logs.error("failed to paging app(pageNo={}, pageSize={}), cause: {}",
                    pageNo, pageSize, Throwables.getStackTraceAsString(e));
            return Response.notOk("app.find.failed");
        }
    }

    @Override
    public Response<Boolean> delete(String appName) {
        try {
            App app = appDao.findByName(appName);
            if (app == null){
                Logs.warn("the app({}) isn't exist when delete.", appName);
                return Response.ok(true);
            }
            return Response.ok(appManager.delete(app.getId()));
        } catch (Exception e){
            Logs.error("failed to delete the app({}), cause: {}",
                    appName, Throwables.getStackTraceAsString(e));
            return Response.notOk("app.delete.failed");
        }
    }
}
