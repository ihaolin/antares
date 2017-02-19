package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum JobType {

    /**
     * Default Timer job
     */
    DEFAULT(1),

    /**
     * Http callback job
     */
    HTTP(2);

    private Integer value;

    JobType(Integer value){
        this.value = value;
    }

    public Integer value(){
        return value;
    }

    public static JobType from(Integer value){

        for (JobType t : JobType.values()){
            if (Objects.equals(t.value, value)){
                return t;
            }
        }

        throw new IllegalStateException("invalid job type value: " + value);
    }
}
