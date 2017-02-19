package me.hao0.antares.server.job;

import org.quartz.*;

public class ContextJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobKey key = context.getJobDetail().getKey();

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String jobSays = dataMap.getString("jobSays");
        float myFloatValue = dataMap.getFloat("myFloatValue");

        System.err.println("Instance " + key + " of ContextJob says: " + jobSays + ", and val is: " + myFloatValue);
    }
}