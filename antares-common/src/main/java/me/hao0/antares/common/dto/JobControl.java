package me.hao0.antares.common.dto;

import me.hao0.antares.common.model.enums.JobState;
import java.io.Serializable;

/**
 * The job control dto
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobControl implements Serializable {

    private static final long serialVersionUID = 8521933124536616448L;

    /**
     * The job id
     */
    private Long id;

    /**
     * The job class
     */
    private String clazz;

    /**
     * The cron expression
     */
    private String cron;

    /**
     * The job desc
     */
    private String desc;

    /**
     * The job scheduler server
     */
    private String scheduler;

    /**
     * The The current fire time;
     */
    private String fireTime;

    /**
     * The previous fire time;
     */
    private String prevFireTime;

    /**
     * The next fire time
     */
    private String nextFireTime;

    /**
     * The running state
     * @see me.hao0.antares.common.model.enums.JobState
     */
    private Integer state;

    /**
     * The running state desc
     */
    private String stateDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public String getFireTime() {
        return fireTime;
    }

    public void setFireTime(String fireTime) {
        this.fireTime = fireTime;
    }

    public String getPrevFireTime() {
        return prevFireTime;
    }

    public void setPrevFireTime(String prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    public String getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(String nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public void setStateAndDesc(JobState state){
        setState(state.value());
        setStateDesc(state.code());
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    @Override
    public String toString() {
        return "JobControl{" +
                "id=" + id +
                ", clazz='" + clazz + '\'' +
                ", cron='" + cron + '\'' +
                ", desc='" + desc + '\'' +
                ", scheduler='" + scheduler + '\'' +
                ", fireTime='" + fireTime + '\'' +
                ", prevFireTime='" + prevFireTime + '\'' +
                ", nextFireTime='" + nextFireTime + '\'' +
                ", state=" + state +
                ", stateDesc='" + stateDesc + '\'' +
                '}';
    }
}
