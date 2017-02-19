package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum JobStatus {

    /**
     * Disable the job
     */
    DISABLE(0),

    /**
     * Enable the job
     */
    ENABLE(1);

    private Integer value;

    JobStatus(Integer value){
        this.value = value;
    }

    public Integer value(){
        return value;
    }

    public static JobStatus from(Integer status){
        for (JobStatus s : JobStatus.values()){
            if (Objects.equals(s.value, status)){
                return s;
            }
        }
        throw new IllegalStateException("invalid job status value: " + status);
    }
}
