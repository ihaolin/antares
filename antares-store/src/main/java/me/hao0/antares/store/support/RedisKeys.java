package me.hao0.antares.store.support;


import me.hao0.antares.common.model.enums.JobInstanceShardStatus;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public final class RedisKeys {

    public static final String REDIS_NAMESPACE_PROP = "antares.redis.namespace";

    public static final String REDIS_NAMESPACE = System.getProperty(REDIS_NAMESPACE_PROP, "ats");

    public static final String KEY_DELIMITER = ":";

    /**
     * Class id list key
     */
    public static final String IDS = "ids";

    /**
     * Class id generator prefix
     */
    public static final String ID_GENERATOR = "idg";

    /**
     * App name mapping
     */
    public static final String APP_INDEX_NAMES = format("apps", "names");

    /**
     * Job & JobConfig mapping
     */
    public static final String JOB_CONFIG_MAPPINGS = format("jobs", "cfg_maps");

    /**
     * Job server relation mapping
     */
    public static final String JOB_SERVER_MAPPINGS = format("jobs", "server_maps");

    /**
     * The key of id generator
     * @param objectPrefix the object prefix
     * @return the key of id generator
     */
    public static String keyOfIdGenerator(String objectPrefix) {
        return format(objectPrefix, ID_GENERATOR);
    }

    /**
     * The key of id generator
     * @param objectPrefix the object prefix
     * @return the key of id generator
     */
    public static String keyOfIds(String objectPrefix) {
        return format(objectPrefix, IDS);
    }

    /**
     * Format impl key
     * @param parts string parts
     * @return db:part1:part2
     */
    public static String format(Object... parts){
        StringBuilder key = new StringBuilder(REDIS_NAMESPACE);
        for (Object part : parts){
            key.append(KEY_DELIMITER).append(part);
        }
        return key.toString();
    }

    /**
     * The key of the app's job names
     * @param appId the app id
     * @return apps:${appId}:job_names
     */
    public static String keyOfAppJobNames(Long appId) {
        return format("apps", appId, "job_names");
    }

    /**
     * The key of the app's jobs
     * @param appId the app id
     * @return apps:${appId}:jobs
     */
    public static String keyOfAppJobs(Long appId) {
        return format("apps", appId, "jobs");
    }

    /**
     * The key of the job's instances
     * @param jobId the job id
     * @return jobs:${jobId}:inss
     */
    public static String keyOfJobInstances(Long jobId) {
        return format("jobs", jobId, "inss");
    }

    /**
     * The key of the app's job class hash mapping
     * @param appId the app id
     * @return apps:${appId}:job_classes
     */
    public static String keyOfAppJobClasses(Long appId) {
        return format("apps", appId, "job_classes");
    }

    /**
     * The key of the server's jobs
     * @param server the server
     * @return servers:${server}:jobs
     */
    public static String keyOfServerJobs(String server) {
        return format("servers", server, "jobs");
    }

    /**
     * The key of the job instance's shards
     * @param jobInstanceId the job instance id
     * @return job_inss:${jobInstanceId}:sds
     */
    public static String keyOfJobInstanceShards(Long jobInstanceId) {
        return format("job_inss", jobInstanceId, "sds");
    }

    /**
     * The key of the job instance's shards set
     * @param jobInstanceId the job instance id
     * @return job_inss:${jobInstanceId}:sds_set
     */
    public static String keyOfJobInstanceShardsSet(Long jobInstanceId) {
        return format("job_inss", jobInstanceId, "sds_set");
    }

    /**
     * The key of the job instance's finish shards set
     * @param jobInstanceId the job instance id
     * @return job_inss:${jobInstanceId}:sds_fset
     */
    public static String keyOfJobInstanceFinishShardsSet(Long jobInstanceId) {
        return format("job_inss", jobInstanceId, "sds_fset");
    }

    /**
     * The key of the job instance's different status's shards set
     * @param jobInstanceId the job instance id
     * @param status the shard status
     * @return job_inss:${jobInstanceId}:sds:${status.value}
     */
    public static String keyOfJobInstanceStatusShards(Long jobInstanceId, JobInstanceShardStatus status) {
        return format("job_inss", jobInstanceId, "sds", status.value());
    }

    /**
     * The key of the client's running shard id list
     * @param client the client, host:pid
     * @return job_ins_sds/${client}
     */
    public static String keyOfClientRunningShards(String client) {
        return format("clients", client, "sds");
    }

    /**
     * The key of the job's next job id set key
     * @param jobId the job id
     * @return jobs:${jobId}:next
     */
    public static String keyOfJobNextJobs(Long jobId) {
        return format("jobs", jobId, "next");
    }
}
