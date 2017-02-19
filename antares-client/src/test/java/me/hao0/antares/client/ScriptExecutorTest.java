package me.hao0.antares.client;

import com.google.common.collect.ImmutableMap;
import me.hao0.antares.client.job.script.DefaultScriptExecutor;
import me.hao0.antares.client.job.script.ScriptExecutor;
import org.junit.Test;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ScriptExecutorTest {

    private ScriptExecutor executor = new DefaultScriptExecutor();

    @Test
    public void testExecuteCommand(){

        executor.exec("ls /", null);

    }

    @Test
    public void testExecuteScript(){
        executor.exec("/Users/haolin/Temp/test_job.sh", ImmutableMap.of("MY_ENV", "HELLO"));
    }

}
