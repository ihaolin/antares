package me.hao0.antares.common.util;

import com.google.common.base.Strings;

/**
 * Zk paths
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public final class ZkPaths {

    /**
     * The default zk namespace
     */
    public static final String DEFAULT_NS = "ats";

    /**
     * The delimiter of path
     */
    private static final String SLASH = "/";

    /**
     * The cluster prefix
     */
    public static final String CLUSTER = "/cluster";

    /**
     * The server leader
     */
    public static final String LEADER = CLUSTER + "/leader";

    /**
     * The alive server list
     */
    public static final String SERVERS = CLUSTER + "/servers";

    /**
     * The alive client list
     */
    public static final String CLIENTS = CLUSTER + "/clients";

    /**
     * The jobs
     */
    public static final String JOBS = "/jobs";

    public static final String JOB_INSTANCES = "/job_inss";

    public static final String SERVER_FAILOVER = "/servers_failover";

    /**
     * Get the path of the server
     * @param server the server host
     * @return /cluster/servers/${server}
     */
    public static String pathOfServer(String server){
        return format(SERVERS, server);
    }

    /**
     * Get the dir of the alive client
     * @param appName the app name of the client
     * @return /cluster/clients/${appName}
     */
    public static String pathOfAppClients(String appName){
        return format(CLIENTS, appName);
    }

    /**
     * Get the dir of the alive client
     * @param appName the app name of the client
     * @param client the client host
     * @return /cluster/clients/${appName}/${client}
     */
    public static String pathOfAppClient(String appName, String client){
        return format(CLIENTS, appName, client);
    }

    /**
     * Get the path of the job
     * @param appName the app name
     * @param jobClass the job class
     * @return /jobs/${appName}/${jobClass}
     */
    public static String pathOfJob(String appName, String jobClass){
        return format(JOBS, appName, jobClass);
    }

    /**
     * Get the path of job running state
     * @param appName the app name
     * @param jobClass the job class
     * @return /jobs/${appName}/${jobClass}/state
     */
    public static String pathOfJobState(String appName, String jobClass){
        return format(JOBS, appName, jobClass, "state");
    }

    /**
     * Get the path of job scheduler
     * @param appName the app name
     * @param jobClass the job class
     * @return /jobs/${appName}/${jobClass}/state
     */
    public static String pathOfJobScheduler(String appName, String jobClass) {
        return format(JOBS, appName, jobClass, "scheduler");
    }

    /**
     * Get the path of job scheduler
     * @param appName the app name
     * @param jobClass the job class
     * @return /jobs/${appName}/${jobClass}/fireTime
     */
    public static String pathOfJobFireTime(String appName, String jobClass) {
        return format(JOBS, appName, jobClass, "fireTime");
    }

    /**
     * Get the path of job instances
     * @param appName the app name
     * @param jobClass the job class
     * @return /jobs/${appName}/${jobClass}/instances
     */
    public static String pathOfJobInstances(String appName, String jobClass){
        return format(JOBS, appName, jobClass, "instances");
    }

    /**
     * Get the path of the job instance
     * @param appName the app name
     * @param jobClass the job class
     * @param instanceId the job instance id
     * @return /jobs/${appName}/${jobClass}/instances/${instanceId}
     */
    public static String pathOfJobInstance(String appName, String jobClass, Long instanceId){
        return format(JOBS, appName, jobClass, "instances", instanceId);
    }

    /**
     * Get the path of job instance lock
     * @param jobInstanceId the job instance id
     * @return the path of job instance lock
     */
    public static String pathOfJobInstanceLock(Long jobInstanceId) {
        return format(JOB_INSTANCES, jobInstanceId);
    }

    /**
     * Get the path of server failover lock
     * @param server the server
     * @return the path of server failover lock
     */
    public static String pathOfServerFailoverLock(String server) {
        return format(SERVER_FAILOVER, server);
    }

    /**
     * Format the path
     * @param parts string parts
     * @return /ats/xxx/yyy
     */
    public static String format(Object... parts){
        StringBuilder key = new StringBuilder();
        for (Object part : parts){
            key.append(SLASH).append(part);
        }
        String strKey = key.toString();
        return strKey.startsWith("//") ? strKey.replace("//", "/") : strKey;
    }

    public static String lastNode(String path){
        if (Strings.isNullOrEmpty(path)){
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
