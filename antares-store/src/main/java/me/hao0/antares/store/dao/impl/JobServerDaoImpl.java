package me.hao0.antares.store.dao.impl;

import com.google.common.collect.Lists;
import me.hao0.antares.common.model.JobServer;
import me.hao0.antares.store.dao.JobServerDao;
import me.hao0.antares.store.support.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Repository
public class JobServerDaoImpl implements JobServerDao {

    @Autowired
    protected StringRedisTemplate redis;

    @Override
    public Boolean bind(JobServer jobServer) {

        String server = jobServer.getServer();
        String jobId = jobServer.getJobId().toString();

        String serverJobsKey = RedisKeys.keyOfServerJobs(server);

        if (redis.opsForSet().add(serverJobsKey, jobId) > 0){
            // bind the job's server
            try {
                redis.opsForHash().put(RedisKeys.JOB_SERVER_MAPPINGS, jobId, server);
            } catch (Exception e){
                redis.opsForSet().remove(serverJobsKey, jobId);
            }
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean unbindJobsOfServer(String server) {
        List<Long> jobIds = findJobsByServer(server);
        // remove all job server mapping
        for (Long jobId : jobIds){
            redis.opsForHash().delete(RedisKeys.JOB_SERVER_MAPPINGS, jobId.toString());
        }
        // delete the server's jobs key
        redis.delete(RedisKeys.keyOfServerJobs(server));
        return Boolean.TRUE;
    }

    @Override
    public Boolean unbindJob(Long jobId) {

        String jobIdStr = jobId.toString();

        String server = String.valueOf(redis.opsForHash().get(RedisKeys.JOB_SERVER_MAPPINGS, jobIdStr));

        // remove the job from the server
        String serverJobsKey = RedisKeys.keyOfServerJobs(server);
        redis.opsForSet().remove(serverJobsKey, jobIdStr);

        // remote the job server mapping
        redis.opsForHash().delete(RedisKeys.JOB_SERVER_MAPPINGS, jobIdStr);

        return Boolean.TRUE;
    }

    @Override
    public String findServerByJobId(Long jobId) {
        Object server = redis.opsForHash().get(RedisKeys.JOB_SERVER_MAPPINGS, jobId.toString());
        if (server == null){
            return null;
        }
        return server.toString();
    }

    @Override
    public List<Long> findJobsByServer(String server) {
        String serverJobsKey = RedisKeys.keyOfServerJobs(server);
        Set<String> jobIdsStr = redis.opsForSet().members(serverJobsKey);

        List<Long> jobIds = Lists.newArrayListWithExpectedSize(jobIdsStr.size());
        for (String jobIdStr : jobIdsStr){
            jobIds.add(Long.valueOf(jobIdStr));
        }

        return jobIds;
    }

    @Override
    public Long countJobsByServer(String server) {
        String serverJobsKey = RedisKeys.keyOfServerJobs(server);
        return redis.opsForSet().size(serverJobsKey);
    }
}
