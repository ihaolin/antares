package me.hao0.antares.client.job.script;

import me.hao0.antares.client.job.JobResult;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface ScriptExecutor {

    /**
     * Execute the script
     * @param command the command (config as the job param)
     * @return return true if
     */
    JobResult exec(String command);

    /**
     * Execute the script
     * @param command the command (config as the job param)
     * @param env the environment
     * @return return true if
     */
    JobResult exec(String command, Map<String, String> env);
}
