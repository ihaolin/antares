package me.hao0.antares.server.api;

import static me.hao0.antares.common.util.ClientUris.*;
import me.hao0.antares.common.dto.JsonResponse;
import me.hao0.antares.common.dto.PullShard;
import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.common.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

/**
 * The client api
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping(value = CLIENTS)
public class Clients {

    @Autowired
    private JobService jobService;

    /**
     * Pull one shard
     */
    @RequestMapping(value = SHARD_PULL, method = RequestMethod.POST)
    public JsonResponse pullShard(
            @RequestParam("instanceId") Long instanceId,
            @RequestParam("client") String client){

        Response<PullShard> pullResp = jobService.pullJobInstanceShard(instanceId, client);
        if (!pullResp.isSuccess()){
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
            @RequestParam("client") String client){

        Response<Boolean> returnResp = jobService.returnJobInstanceShard(instanceId, shardId, client);
        if (!returnResp.isSuccess()){
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
            @RequestParam(value = "cause", defaultValue = "") String cause){

        ShardFinishDto shardFinishDto = buildShardFinishDto(client, shardId, instanceId, startTime, endTime, success, cause);

        Response<Boolean> pullResp = jobService.finishJobInstanceShard(shardFinishDto);
        if (!pullResp.isSuccess()){
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
        if (!success){
            shardFinishDto.setCause(cause);
        }

        return shardFinishDto;
    }
}
