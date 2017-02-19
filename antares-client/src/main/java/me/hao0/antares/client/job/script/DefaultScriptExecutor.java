package me.hao0.antares.client.job.script;

import com.google.common.base.Throwables;
import me.hao0.antares.client.job.JobResult;
import me.hao0.antares.common.util.Constants;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

/**
 * The script executor
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class DefaultScriptExecutor implements ScriptExecutor {

    private static final Logger log = LoggerFactory.getLogger(DefaultScriptExecutor.class);

    @Override
    public JobResult exec(String command) {
        return exec(command, null);
    }

    @Override
    public JobResult exec(String command, Map<String, String> env) {

        try {

            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.execute(cmdLine, env);

            return JobResult.SUCCESS;
        } catch (Exception e) {

            String error = Throwables.getStackTraceAsString(e);
            log.error("failed to execute command(cmd={}, env={}), cause: {}",
                    command, env, error);
            if (error.length() > Constants.MAX_ERROR_LENGTH){
                error = error.substring(0, Constants.MAX_ERROR_LENGTH);
            }

            return JobResult.failed(error);
        }
    }
}
