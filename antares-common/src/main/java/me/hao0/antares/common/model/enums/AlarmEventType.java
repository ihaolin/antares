package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * The alarm event type
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum AlarmEventType {

    /**
     * The event, which the job executing timeout
     */
    JOB_TIMEOUT(100, "alarm.type.job.timeout"),

    /**
     * The event, which the job executing failed
     */
    JOB_FAILED(101, "alarm.type.job.failed");

    private Integer value;

    private String code;

    AlarmEventType(Integer value, String code){
        this.value = value;
        this.code = code;
    }

    public Integer value(){
        return value;
    }

    public String code(){
        return code;
    }

    public static AlarmEventType from(Integer value){

        for (AlarmEventType t : AlarmEventType.values()){
            if (Objects.equals(t.value, value)){
                return t;
            }
        }

        throw new IllegalStateException("invalid alarm event type: " + value);
    }
}
