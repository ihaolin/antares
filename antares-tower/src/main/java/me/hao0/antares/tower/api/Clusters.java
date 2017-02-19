package me.hao0.antares.tower.api;

import me.hao0.antares.common.dto.ClientInfo;
import me.hao0.antares.common.dto.JsonResponse;
import me.hao0.antares.common.dto.ServerInfo;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.Job;
import me.hao0.antares.store.service.ClusterService;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.store.util.Response;
import me.hao0.antares.tower.support.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping("/api")
public class Clusters {

    @Autowired
    private JobService jobService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private Messages messages;

    @RequestMapping(value = "/servers", method = RequestMethod.GET)
    public JsonResponse servers(){

        Response<List<ServerInfo>> serversResp = clusterService.listServers();
        if (!serversResp.isSuccess()){
            Logs.error("failed to list servers, cause: {}", serversResp.getErr());
            return JsonResponse.notOk(messages.get("servers.list.failed"));
        }

        return JsonResponse.ok(serversResp.getData());
    }

    @RequestMapping(value = "/servers/jobs", method = RequestMethod.GET)
    public JsonResponse listJobsByServer(@RequestParam("server") String server){

        Response<List<Job>> findResp = jobService.findJobsByServer(server);
        if (!findResp.isSuccess()){
            return JsonResponse.notOk(messages.get(findResp.getErr()));
        }

        return JsonResponse.ok(findResp.getData());
    }

    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public JsonResponse clients(@RequestParam("appId") Long appId){

        Response<List<ClientInfo>> clientsResp = clusterService.listClients(appId);
        if (!clientsResp.isSuccess()){
            Logs.error("failed to list clients of app(id={}), cause: {}", appId, clientsResp.getErr());
            return JsonResponse.notOk(messages.get("clients.list.failed"));
        }

        return JsonResponse.ok(clientsResp.getData());
    }
}
