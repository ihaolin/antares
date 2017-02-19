package me.hao0.antares.store.service;

import me.hao0.antares.common.dto.ClientInfo;
import me.hao0.antares.common.dto.ServerInfo;
import me.hao0.antares.store.util.Response;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ClusterService {

    /**
     * List the clients of the app
     * @param appId the app name
     * @return the clients list of the app
     */
    Response<List<ClientInfo>> listClients(Long appId);

    /**
     * List the cluster's server list
     * @return the cluster's server list
     */
    Response<List<ServerInfo>> listServers();

    /**
     * List the cluster's server string list
     * @return the cluster's server string list
     */
    Response<List<String>> listSimpleServers();

}
