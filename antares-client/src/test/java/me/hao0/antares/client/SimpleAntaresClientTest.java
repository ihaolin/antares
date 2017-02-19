package me.hao0.antares.client;

import me.hao0.antares.client.core.SimpleAntaresClient;
import me.hao0.antares.client.job.DemoJob;
import me.hao0.antares.client.job.HelloJob;
import me.hao0.antares.client.job.MyScriptJob;
import me.hao0.antares.client.job.script.ScriptJob;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SimpleAntaresClientTest {

    private SimpleAntaresClient client;

    @Before
    public void init(){
        client = new SimpleAntaresClient("test_app", "123456", "localhost:2181", "ats");
        client.setExecutorThreadCount(32);
        client.start();
    }


    @Test
    public void testRegisterJob() throws InterruptedException {

        HelloJob helloJob = new HelloJob();
        DemoJob demoJob = new DemoJob();
        ScriptJob scriptJob = new MyScriptJob();

        client.registerJob(helloJob);
        client.registerJob(demoJob);
        client.registerJob(scriptJob);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @After
    public void destroy(){
        client.shutdown();
    }
}
