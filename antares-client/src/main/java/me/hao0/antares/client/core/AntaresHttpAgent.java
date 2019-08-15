package me.hao0.antares.client.core;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import me.hao0.antares.client.exception.AuthFailException;
import me.hao0.antares.client.exception.Server503Exception;
import me.hao0.antares.common.balance.LoadBalance;
import me.hao0.antares.common.balance.RandomLoadBalance;
import me.hao0.antares.common.dto.*;
import me.hao0.antares.common.http.Http;
import me.hao0.antares.common.http.HttpMethod;
import me.hao0.antares.common.model.enums.ShardOperateRespCode;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.*;

import static me.hao0.antares.common.util.Constants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AntaresHttpAgent extends Component implements Lifecycle {

    private static final Logger log = LoggerFactory.getLogger(AntaresHttpAgent.class);

    private final AbstractAntaresClient client;

    /**
     * The current using server
     */
    private volatile String currentServer;

    /**
     * The request headers
     */
    private final Map<String, String> headers = Maps.newHashMap();

    private final LoadBalance<String> balancer = new RandomLoadBalance<>();

    public AntaresHttpAgent(AbstractAntaresClient client){
        this.client = client;
    }

    public void setCurrentServer(String server) {
        this.currentServer = server;
    }

    @Override
    public void doStart() {

        // select a server
        selectServer();

        // init header
        initHttpHeaders();
    }

    private void initHttpHeaders() {
        headers.put(APP_NAME_HEADER, client.getAppName());
        headers.put(APP_KEY_HEADER, client.getAppSecret());
        headers.put(CLIENT_LANG_HEADER, Langs.JAVA.ordinal() + "");
        headers.put(CLIENT_VERSION_HEADER, client.getClientVersion());
    }

    @Override
    public void doShutdown() {

    }

    private Boolean selectServer() {

        List<String> servers = client.getHttpServers();

        if (servers.isEmpty()){
            log.warn("There are no available server, please check the environment.");
            return Boolean.FALSE;
        }

        currentServer = balancer.balance(servers);

        return Boolean.TRUE;
    }

    <T> T doGet(String uri, Map<String, String> headers, Map<String, Object> params, Class<T> targetType){
        for (;;){
            try {
                return doGet(currentServer, uri, headers, params, targetType);
            } catch (AuthFailException e){
                throw new IllegalArgumentException("appKey isn't right, please check");
            } catch (Server503Exception | HttpRequest.HttpRequestException e){
                Sleeps.sleep(2);
                String oldServer = currentServer;
                log.warn("current server({}) is unavailable, try to select a new server", oldServer);
                if(selectServer()){
                    log.info("old server({}) is unavailable, routed a select server({})", oldServer, currentServer);
                } else {
                    log.warn("failed to select a new server, please check the server cluster");
                    Sleeps.sleep(5);
                }
            }
        }
    }

    private <T> T doGet(String server, String uri, Map<String, String> headers, Map<String, Object> params, Class<T> targetType){
        Map<String, Object> respMap = doRequest(server, uri, HttpMethod.GET, headers, params, 0);
        if (respMap == null || respMap.isEmpty()){
            return null;
        }
        return Jacksons.fromMap(respMap, targetType);
    }

    <T> T doPost(String uri, Map<String, String> headers, Map<String, Object> params, int readTimeout, Class<T> targetType){
        for (;;){
            try {
                return doPost(currentServer, uri, headers, params, readTimeout, targetType);
            } catch (AuthFailException e){
                throw new IllegalArgumentException("appKey isn't right, please check");
            } catch (Server503Exception | HttpRequest.HttpRequestException e){
                String oldServer = currentServer;
                log.warn("current server({}) is maybe unavailable, trying to selects a new server", oldServer);
                selectServer();
                log.warn("old server({}) is unavailable, select a new server({})", oldServer, currentServer);
            }
        }
    }

    private <T> T doPost(String server, String uri, Map<String, String> headers, Map<String, Object> params, int readTimeout, Class<T> targetType){
        Map<String, Object> respMap = doPostAsMap(server, uri, headers, params, readTimeout);
        if (respMap == null || respMap.isEmpty()){
            return null;
        }
        return Jacksons.fromMap(respMap, targetType);
    }

    private Map<String, Object> doPostAsMap(String uri, Map<String, String> headers, Map<String, Object> params, int readTimeout){
        return doPostAsMap(currentServer, uri, headers, params, readTimeout);
    }

    private Map<String, Object> doPostAsMap(String server, String uri, Map<String, String> headers, Map<String, Object> params, int readTimeout){
        for (;;){
            try {
                Map<String, Object> respMap = doRequest(server, uri, HttpMethod.POST, headers, params, readTimeout);
                if (respMap == null || respMap.isEmpty()){
                    return null;
                }
                return respMap;
            } catch (AuthFailException e){
                throw new IllegalArgumentException("appKey isn't right, please check");
            } catch (Server503Exception | HttpRequest.HttpRequestException e){
                // there are no servers available
                Sleeps.sleep(3);
                String oldServer = currentServer;
                log.warn("current server({}) is maybe unavailable, trying to selects a new server", currentServer);
                selectServer();
                log.warn("old server({}) is unavailable, select a new server({})", oldServer, currentServer);
                server = currentServer;
            }
        }
    }

    private Map<String, Object> doRequest(String server, String uri, HttpMethod method,
                            Map<String, String> headers, Map<String, Object> params, int readTimeout){

        String reqUri = HTTP_PREFIX + server + ClientUris.CLIENTS + uri;

        Http http;
        if (method == HttpMethod.GET){
            http = Http.get(reqUri);
        } else {
            http = Http.post(reqUri);
        }

        if (readTimeout > 0){
            http.readTimeout(readTimeout);
        }

        if (headers != null){
            http.headers(headers);
        }

        String resp = http.params(params).request();
        if (Strings.isNullOrEmpty(resp)){
            return null;
        }

        return checkRespErr(resp);
    }

    private Map<String, Object> checkRespErr(String respJson) {
        Map<String, Object> resp = Jacksons.toMap(respJson);
        Integer status = getRespStatus(resp);
        if (Objects.equal(JsonResponse.SERVER_ERR.getStatus(), status)){
            throw new Server503Exception();
        } else if (Objects.equal(JsonResponse.AUTH_FAIL.getStatus(), status)){
            throw new AuthFailException();
        }
        return resp;
    }

    /**
     * Pull the job instance's shard
     * @param jobInstanceId the job instance id
     * @return the pull shard resp
     */
    public ShardPullResp pullJobInstanceShard(Long jobInstanceId) {

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("client", Systems.hostPid());
        params.put("instanceId", jobInstanceId);

        Map<String, Object> respMap = doPostAsMap(ClientUris.SHARD_PULL, headers, params, 0);
        if (respMap == null){
            return null;
        }

        Integer status = getRespStatus(respMap);
        if (Objects.equal(JsonResponse.OK, status)){
            // ok
            PullShard shard = Jacksons.fromMap((Map<?, ?>)respMap.get("data"), PullShard.class);
            return new ShardPullResp(null, shard);
        }

        // business error
        Integer code = getErrCode(respMap);
        return new ShardPullResp(ShardOperateRespCode.from(code), null);
    }

    /**
     * Return the job instance's shard
     * @param jobInstanceId the job instance id
     * @param shardId the shard id
     * @return the finish shard resp
     */
    public ShardOperateResp returnJobInstanceShard(Long jobInstanceId, Long shardId) {

        Map<String, Object> params = Maps.newHashMapWithExpectedSize(3);
        params.put("instanceId", jobInstanceId);
        params.put("shardId", shardId);
        params.put("client", Systems.hostPid());

        Map<String, Object> respMap = doPostAsMap(ClientUris.SHARD_RETURN, headers, params,  0);
        if (respMap == null){
            return null;
        }

        Integer status = getRespStatus(respMap);
        if (Objects.equal(JsonResponse.OK, status)){
            // ok
            Boolean success = Boolean.valueOf(respMap.get("data").toString());
            return new ShardOperateResp(null, success);
        }

        // business error
        Integer code = getErrCode(respMap);
        return new ShardOperateResp(ShardOperateRespCode.from(code), null);
    }

    /**
     * Finish the job instance's shard
     * @param shardFinishDto the job instance id
     * @return the finish shard resp
     */
    public ShardOperateResp finishJobInstanceShard(ShardFinishDto shardFinishDto) {

        Map<String, Object> params = Jacksons.toMap(shardFinishDto);

        Map<String, Object> respMap = doPostAsMap(ClientUris.SHARD_FINISH, headers, params,  0);
        if (respMap == null){
            return null;
        }

        Integer status = getRespStatus(respMap);
        if (Objects.equal(JsonResponse.OK, status)){
            // ok
            Boolean success = Boolean.valueOf(respMap.get("data").toString());
            return new ShardOperateResp(null, success);
        }

        // business error
        Integer code = getErrCode(respMap);
        return new ShardOperateResp(ShardOperateRespCode.from(code), null);
    }

    protected Integer getRespStatus(Map<String, Object> respMap) {
        return Integer.valueOf(respMap.get("status").toString());
    }

    protected Integer getErrCode(Map<String, Object> respMap) {
        return Integer.valueOf(respMap.get("err").toString());
    }
}
