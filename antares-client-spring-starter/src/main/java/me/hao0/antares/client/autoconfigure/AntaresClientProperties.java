package me.hao0.antares.client.autoconfigure;

import me.hao0.antares.common.util.Systems;
import me.hao0.antares.common.util.ZkPaths;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties of antares client
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
@ConfigurationProperties("antares")
public class AntaresClientProperties {

    private String appName;

    private String appSecret;

    private String zkServers = "localhost:2181";

    private String zkNamespace = ZkPaths.DEFAULT_NS;

    private Integer executorThreadCount = Systems.cpuNum() * 2;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getZkServers() {
        return zkServers;
    }

    public void setZkServers(String zkServers) {
        this.zkServers = zkServers;
    }

    public String getZkNamespace() {
        return zkNamespace;
    }

    public void setZkNamespace(String zkNamespace) {
        this.zkNamespace = zkNamespace;
    }

    public Integer getExecutorThreadCount() {
        return executorThreadCount;
    }

    public void setExecutorThreadCount(Integer executorThreadCount) {
        this.executorThreadCount = executorThreadCount;
    }

    @Override
    public String toString() {
        return "AntaresClientProperties{" +
                "appName='" + appName + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", zkServers='" + zkServers + '\'' +
                ", zkNamespace='" + zkNamespace + '\'' +
                ", executorThreadCount=" + executorThreadCount +
                '}';
    }
}
