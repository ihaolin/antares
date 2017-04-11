package me.hao0.antares.alarm.alarmer;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AlarmContext {

    private String subject;

    private String body;

    private String appName;

    private String jobName;

    private String scheduler;

    private String detail;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "AlarmContext{" +
                "subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", appName='" + appName + '\'' +
                ", jobName='" + jobName + '\'' +
                ", scheduler='" + scheduler + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
