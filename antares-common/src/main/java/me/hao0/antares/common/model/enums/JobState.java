package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * The job running state
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum JobState {

    /**
     * Disable
     */
    DISABLE(0, "job.state.disable"),

    /**
     * There are no job instances
     */
    WAITING(1, "job.state.waiting"),

    /**
     * There are job instances
     */
    RUNNING(2, "job.state.running"),

    /**
     * Enable, but no scheduler
     */
    STOPPED(3, "job.state.stopped"),

    /**
     * Failed to execute the latest job instance
     */
    FAILED(4, "job.state.failed"),

    /**
     * The job is paused
     */
    PAUSED(5, "job.state.paused");

    private Integer value;

    private String code;

    JobState(Integer value, String code){
        this.value = value;
        this.code = code;
    }

    public Integer value(){
        return value;
    }

    public String code(){
        return code;
    }

    public static JobState from(Integer state){
        for (JobState s : JobState.values()){
            if (Objects.equals(s.value, state)){
                return s;
            }
        }
        throw new IllegalStateException("invalid job state value: " + state);
    }

    public static Boolean isScheduling(JobState state) {
        return state == WAITING
                || state == RUNNING
                || state == PAUSED
                || state == FAILED;
    }
}
