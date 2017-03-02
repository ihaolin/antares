package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * The dependency job dto
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class DependenceJob implements Serializable {

    private static final long serialVersionUID = -6905089976691794301L;

    /**
     * The job id
     */
    private Long id;

    /**
     * The app name
     */
    private String appName;

    /**
     * The job name
     */
    private String jobClass;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    @Override
    public String toString() {
        return "DependenceJob{" +
                "id=" + id +
                ", appName='" + appName + '\'' +
                ", jobClass='" + jobClass + '\'' +
                '}';
    }
}
