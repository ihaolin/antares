package me.hao0.antares.server.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.err.println("start hello job...");
            try {
                Thread.sleep(70000L);
                System.err.println("end hello job...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }