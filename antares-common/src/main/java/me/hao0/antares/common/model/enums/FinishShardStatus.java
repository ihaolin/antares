package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * Pull shard staths
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum FinishShardStatus {

    /**
     * The job instance doesn't exist
     */
    INSTANCE_NOT_EXIST(0),

    /**
     * The job instance has finished
     */
    INSTANCE_FINISH(1),

    /**
     * Is not the shard puller
     */
    NOT_OWNER(2),

    /**
     * Pull a shard successfully
     */
    FINISH_SUCCESS(3),

    /**
     * Pull failed
     */
    FINISH_FAILED(4),

    /**
     * The shard not exist
     */
    SHARD_NOT_EXIST(6);

    private Integer value;

    FinishShardStatus(Integer value){
        this.value = value;
    }

    public Integer value(){
        return value;
    }

    public static FinishShardStatus from(Integer value){
        for (FinishShardStatus s : FinishShardStatus.values()){
            if (Objects.equals(s.value, value)){
                return s;
            }
        }
        throw new IllegalStateException("invalid pull shard status value: " + value);
    }

    /**
     * Need finish again
     * @param value the pull shard status value
     * @return return true if pull again, or false
     */
    public static Boolean needFinish(Integer value){
        FinishShardStatus status = from(value);
        return status == FINISH_FAILED;
    }
}
