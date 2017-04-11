package me.hao0.antares.alarm.config;

import me.hao0.antares.common.log.Logs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class AlarmConfig {

    @Value("${antares.alarm.enable:false}")
    private Boolean enable;

    @Value("${antares.alarm.notifyType:}")
    private Integer notifyType;

    @Value("${antares.alarm.subject:}")
    private String subject;

    @Value("${antares.alarm.template.jobTimeout:}")
    private String jobTimeoutTemplate;

    @Value("${antares.alarm.template.jobFailed:}")
    private String jobFailedTemplate;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(Integer notifyType) {
        this.notifyType = notifyType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getJobTimeoutTemplate() {
        return jobTimeoutTemplate;
    }

    public void setJobTimeoutTemplate(String jobTimeoutTemplate) {
        this.jobTimeoutTemplate = jobTimeoutTemplate;
    }

    public String getJobFailedTemplate() {
        return jobFailedTemplate;
    }

    public void setJobFailedTemplate(String jobFailedTemplate) {
        this.jobFailedTemplate = jobFailedTemplate;
    }

    @PostConstruct
    public void init(){
        Logs.info("Alarm config: {}", this);
    }

    @Override
    public String toString() {
        return "AlarmConfig{" +
                "enable=" + enable +
                ", notifyType=" + notifyType +
                ", subject='" + subject + '\'' +
                ", jobTimeoutTemplate='" + jobTimeoutTemplate + '\'' +
                ", jobFailedTemplate='" + jobFailedTemplate + '\'' +
                '}';
    }
}
