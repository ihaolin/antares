package me.hao0.antares.tower.api;

import me.hao0.antares.common.dto.AppDeleteDto;
import me.hao0.antares.common.dto.AppSaveDto;
import me.hao0.antares.common.dto.JsonResponse;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.App;
import me.hao0.antares.store.service.AppService;
import me.hao0.antares.store.util.Page;
import me.hao0.antares.common.util.Response;
import me.hao0.antares.tower.support.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping("/api/apps")
public class Apps {

    @Autowired
    private Messages messages;

    @Autowired
    private AppService appService;

    /**
     * Paging the apps
     * @param pageNo the page no
     * @param pageSize the page size
     * @param appName the app full name
     * @return the app page data response
     */
    @RequestMapping(method = RequestMethod.GET)
    public JsonResponse pagingApp(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "appName", defaultValue = "") String appName){

        Response<Page<App>> pagingResp = appService.pagingApp(appName, pageNo, pageSize);
        if (!pagingResp.isSuccess()){
            return JsonResponse.notOk(messages.get(pagingResp.getErr()));
        }

        return JsonResponse.ok(pagingResp.getData());
    }

    /**
     * Save the app
     */
    @RequestMapping(method = RequestMethod.POST)
    public JsonResponse saveApp(@RequestBody AppSaveDto appSaveDto){

        App app = new App();
        app.setAppName(appSaveDto.getAppName());
        app.setAppKey(appSaveDto.getAppKey());
        app.setAppDesc(appSaveDto.getAppDesc());

        Response<Long> saveResp = appService.save(app);
        if (!saveResp.isSuccess()){
            Logs.error("failed to save app({}), cause: {}", app, saveResp.getErr());
            return JsonResponse.notOk(saveResp.getErr());
        }

        // inherit the app's jobs

        return JsonResponse.ok(saveResp.getData());
    }

    /**
     * Delete the app
     */
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public JsonResponse delApp(@RequestBody AppDeleteDto appDeleteDto){

        Response<Boolean> delResp = appService.delete(appDeleteDto.getAppName());
        if (!delResp.isSuccess()){
            Logs.error("failed to delete app({}), cause: {}", appDeleteDto.getAppName(), delResp.getErr());
        }

        return JsonResponse.ok(delResp.getData());
    }
}
