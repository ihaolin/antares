package me.hao0.antares.store;

import me.hao0.antares.common.model.App;
import me.hao0.antares.common.model.Job;
import me.hao0.antares.common.model.JobConfig;
import me.hao0.antares.common.model.JobServer;
import me.hao0.antares.common.model.enums.JobStatus;
import me.hao0.antares.common.model.enums.JobType;
import me.hao0.antares.store.dao.AppDao;
import me.hao0.antares.store.dao.JobConfigDao;
import me.hao0.antares.store.dao.JobDao;
import me.hao0.antares.store.dao.JobServerDao;
import me.hao0.antares.store.manager.AppManager;
import me.hao0.antares.store.manager.JobConfigManager;
import me.hao0.antares.store.manager.JobManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AllInOneTest extends BaseTest{

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private AppManager appManager;

    @Autowired
    private JobManager jobManager;

    @Autowired
    private JobConfigManager jobConfigManager;

    @Autowired
    private AppDao appDao;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private JobConfigDao jobConfigDao;

    @Autowired
    private JobServerDao jobServerDao;

    @Test
    public void flushData(){

        redis.getConnectionFactory().getConnection().flushAll();

        // create app
        App app = new App();
        app.setAppName("test_app");
        app.setAppKey("123456");
        app.setAppDesc("测试应用");
        assertTrue(appManager.save(app));
        assertNotNull(appDao.findByName(app.getAppName()));

        Job job = initJob(app, "me.hao0.antares.client.job.HelloJob", "0 0/1 * * * ?", "hello任务", null, 4, "0=0;1=1;2=2;3=3");
        bindJobServer(job);

        job = initJob(app, "me.hao0.antares.client.job.DemoJob", "0/30 * * * * ?", "demo任务", null, 2, "0=0;1=1");
        bindJobServer(job);

        job = initJob(app, "me.hao0.antares.client.job.MyScriptJob", "0/30 * * * * ?", "脚本任务", "ls /Users/haolin", 1, "0=192.168.1.100");
        bindJobServer(job);

        assertEquals(3, jobServerDao.countJobsByServer("127.0.0.1:22122").intValue());
    }

    private void bindJobServer(Job job) {
        jobServerDao.bind(new JobServer(job.getId(), "127.0.0.1:22122"));
    }

    private Job initJob(App app, String jobClass, String cron, String desc, String jobParam, Integer shardCount, String shardParams) {
        // create job
        Job job = new Job();
        job.setAppId(app.getId());
        job.setClazz(jobClass);
        job.setCron(cron);
        job.setStatus(JobStatus.ENABLE.value());
        job.setType(JobType.DEFAULT.value());
        job.setDesc(desc);
        assertTrue(jobManager.save(job));
        assertNotNull(jobDao.findByJobClass(app.getId(), jobClass));

        // create job config
        JobConfig config = new JobConfig();
        config.setJobId(job.getId());
        config.setMisfire(Boolean.TRUE);
        config.setShardCount(shardCount);
        config.setShardParams(shardParams);
        config.setMaxShardPullCount(3);
        config.setParam(jobParam);

        assertTrue(jobConfigManager.save(config));
        assertNotNull(jobConfigDao.findByJobId(config.getJobId()));
        return job;
    }
}
