package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum JobInstanceStatus {

    /**
     * The job instance is created
     */
    NEW(1, "job.instance.status.new"),

    /**
     * The job instance is running
     */
    RUNNING(2, "job.instance.status.running"),

    /**
     * The job instance executed successfully
     */
    SUCCESS(3, "job.instance.status.success"),

    /**
     * The job instance executed failed
     */
    FAILED(4, "job.instance.status.failed"),

    /**
     * The job instance is forced to be terminated
     */
    TERMINATED(5, "job.instance.status.terminated"),

    /**
     * The job instance is timeout
     */
    TIMEOUT_CLOSED(6, "job.instance.status.timeout");

    private Integer value;

    private String code;

    JobInstanceStatus(Integer value, String code){
        this.value = value;
        this.code = code;
    }

    public Integer value(){
        return value;
    }

    public String code(){
        return code;
    }

    public static JobInstanceStatus from(Integer status){
        for (JobInstanceStatus s : JobInstanceStatus.values()){
            if (Objects.equals(s.value, status)){
                return s;
            }
        }
        throw new IllegalStateException("invalid job instance status value: " + status);
    }

    public static boolean isFinal(Integer status) {
        JobInstanceStatus instanceStatus = from(status);
        return instanceStatus == SUCCESS
                || instanceStatus == FAILED
                || instanceStatus == TERMINATED
                || instanceStatus == TIMEOUT_CLOSED;
    }
}
