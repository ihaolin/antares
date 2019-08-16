package me.hao0.antares.server.api;

import me.hao0.antares.common.dto.JobEditDto;
import me.hao0.antares.common.dto.JsonResponse;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.model.App;
import me.hao0.antares.common.model.Job;
import me.hao0.antares.common.util.Response;
import me.hao0.antares.store.service.AppService;
import me.hao0.antares.store.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static me.hao0.antares.common.util.ClientUris.*;

/**
 * The client api
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping(value = CLIENTS)
public class Clients {

    private Logger log = LoggerFactory.getLogger(Clients.class);

    private AppService appService;
    private JobService jobService;

    /**
     * Register app and it's jobs
     */
    @PostMapping(REGISTER)
    public void register(
            HttpServletRequest request,
            @RequestParam("appName") String appName,
            @RequestParam("appKey") String appKey,
            @RequestParam(value = "jobs[]", required = false) String[] jobs) {

        log.info("Client [{}] register app [{}] and it's jobs {}", request.getRemoteAddr(), appName, jobs);

        App app = appService.findByName(appName).getData();
        if (app == null) {
            app = new App();
            app.setAppName(appName);
            app.setAppKey(appKey);
            Response<Long> save = appService.save(app);
            app.setId(save.getData());
        }

        if (jobs != null) {
            for (String jobClass : jobs) {
                JobEditDto job = new JobEditDto();
                job.setAppId(app.getId());
                job.setStatus(false);
                job.setClazz(jobClass);
                job.setMaxShardPullCount(3);
                job.setShardCount(1);
                job.setMisfire(true);
                jobService.saveJob(job);
            }
        }
    }

    /**
     * Pull one shard
     */
    @RequestMapping(value = SHARD_PULL, method = RequestMethod.POST)
    public JsonResponse pullShard(
            @RequestParam("instanceId") Long instanceId,
            @RequestParam("client") String client) {

        Response<PullShard> pullResp = jobService.pullJobInstanceShard(instanceId, client);
        if (!pullResp.isOk()) {
            return JsonResponse.notOk(pullResp.getStatus(), pullResp.getErr());
        }

        return JsonResponse.ok(pullResp.getData());
    }

    /**
     * Return one shard
     */
    @RequestMapping(value = SHARD_RETURN, method = RequestMethod.POST)
    public JsonResponse returnShard(
            @RequestParam("instanceId") Long instanceId,
            @RequestParam("shardId") Long shardId,
            @RequestParam("client") String client) {

        Response<Boolean> returnResp = jobService.returnJobInstanceShard(instanceId, shardId, client);
        if (!returnResp.isOk()) {
            return JsonResponse.notOk(returnResp.getStatus(), returnResp.getErr());
        }

        return JsonResponse.ok(returnResp.getData());
    }

    /**
     * Pull one shard
     */
    @RequestMapping(value = SHARD_FINISH, method = RequestMethod.POST)
    public JsonResponse finishShard(
            @RequestParam("client") String client,
            @RequestParam("shardId") Long shardId,
            @RequestParam("instanceId") Long instanceId,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime,
            @RequestParam(value = "success", defaultValue = "true") Boolean success,
            @RequestParam(value = "cause", defaultValue = "") String cause) {

        ShardFinishDto shardFinishDto = buildShardFinishDto(client, shardId, instanceId, startTime, endTime, success, cause);

        Response<Boolean> pullResp = jobService.finishJobInstanceShard(shardFinishDto);
        if (!pullResp.isOk()) {
            return JsonResponse.notOk(pullResp.getStatus(), pullResp.getErr());
        }

        return JsonResponse.ok(pullResp.getData());
    }

    private ShardFinishDto buildShardFinishDto(String client, Long shardId, Long instanceId,
                                               Long startTime, Long endTime, Boolean success, String cause) {

        ShardFinishDto shardFinishDto = new ShardFinishDto();

        shardFinishDto.setClient(client);
        shardFinishDto.setShardId(shardId);
        shardFinishDto.setInstanceId(instanceId);
        shardFinishDto.setStartTime(new Date(startTime));
        shardFinishDto.setEndTime(new Date(endTime));
        shardFinishDto.setSuccess(success);
        if (!success) {
            shardFinishDto.setCause(cause);
        }

        return shardFinishDto;
    }

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @Autowired
    public void setJobService(JobService jobService) {
        this.jobService = jobService;
    }
}
