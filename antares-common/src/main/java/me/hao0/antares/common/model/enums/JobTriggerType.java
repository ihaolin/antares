package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum JobTriggerType {

    /**
     * Triggered by Quartz scheduler
     */
    DEFAULT(1, "job.trigger.type.default"),

    /**
     * Triggered by Tower API
     */
    API(2, "job.trigger.type.api"),

    /**
     * Triggered by dependency notify
     */
    NOTIFY(3, "job.trigger.type.notify");

    private Integer value;

    private String code;

    JobTriggerType(Integer value, String code){
        this.value = value;
        this.code = code;
    }

    public Integer value(){
        return value;
    }

    public String code(){
        return code;
    }

    public static JobTriggerType from(Integer value){

        for (JobTriggerType t : JobTriggerType.values()){
            if (Objects.equals(t.value, value)){
                return t;
            }
        }

        throw new IllegalStateException("invalid job trigger type value: " + value);
    }
}
