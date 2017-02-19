package me.hao0.antares.common.dto;

import me.hao0.antares.common.model.App;
import me.hao0.antares.common.model.Job;
import me.hao0.antares.common.model.JobConfig;
import java.io.Serializable;

/**
 * The job detail
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobDetail implements Serializable {

    private static final long serialVersionUID = 2059649679491717955L;

    private App app;

    private Job job;

    private JobConfig config;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public JobConfig getConfig() {
        return config;
    }

    public void setConfig(JobConfig config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "JobDetail{" +
                "app=" + app +
                ", job=" + job +
                ", config=" + config +
                '}';
    }
}
