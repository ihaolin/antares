package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * Pull shard staths
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum ShardOperateRespCode {

    /**
     * The job instance doesn't exist
     */
    INSTANCE_NOT_EXIST(0),

    /**
     * The job instance is final
     */
    INSTANCE_IS_FINAL(1),


    /******* FOR SHARD PULL *******/
    /**
     * All shards are pulled
     */
    SHARD_NO_AVAILABLE(2),

    /**
     * Pull a shard successfully
     */
    SHARD_PULL_SUCCESS(3),

    /**
     * Pull failed
     */
    SHARD_PULL_FAILED(4),

    /**
     * The shard pull count exceeds max pull count
     */
    SHARD_PULL_COUNT_EXCEED(5),

    /******* FOR SHARD FINISH *******/
    /**
     * Is not the shard puller when push back
     */
    SHARD_NOT_PULLER(6),

    /**
     * Finish success
     */
    SHARD_FINISH_SUCCESS(7),

    /**
     * Finish fail
     */
    SHARD_FINISH_FAILED(8),

    /**
     * The shard not exist
     */
    SHARD_NOT_EXIST(9),

    /**
     * The shard create fail
     */
    SHARD_CREATE_FAILED(10),

    /**
     * The shard status is final
     */
    SHARD_FINAL(11),

    /******* FOR SHARD PUSH *******/

    /**
     * Return the shard successfully
     */
    SHARD_RETURN_SUCCESS(12),

    /**
     * Return the shard failed
     */
    SHARD_RETURN_FAILED(13),

    /**
     * The client ip isn't allowed to execute job
     */
    IP_NOT_ASSIGNED(14);

    private Integer value;

    ShardOperateRespCode(Integer value){
        this.value = value;
    }

    public Integer value(){
        return value;
    }

    public static ShardOperateRespCode from(Integer value){
        for (ShardOperateRespCode s : ShardOperateRespCode.values()){
            if (Objects.equals(s.value, value)){
                return s;
            }
        }
        throw new IllegalStateException("invalid pull shard status value: " + value);
    }

    /**
     * Need pull again
     * @param code the response code
     * @return return true if pull again, or false
     */
    public static Boolean needPullAgain(ShardOperateRespCode code){

        return (code == SHARD_NO_AVAILABLE
                || code == SHARD_PULL_FAILED);
    }

    /**
     * Need finish again or not
     * @param code the response code
     * @return return true if pull again, or false
     */
    public static Boolean needFinishAgain(ShardOperateRespCode code){
        return code == SHARD_FINISH_FAILED;
    }

    /**
     * Need return again or not
     * @param code the response code
     * @return return true if push again, or false
     */
    public static Boolean needReturnAgain(ShardOperateRespCode code){
        return code == SHARD_RETURN_FAILED;
    }

    /**
     * Need clean the job instance or not
     * @param code the shard operate response code
     * @return return true if need clean job instance, thinking that the dirty job instance data in zk
     */
    public static Boolean needCleanJobInstance(ShardOperateRespCode code) {
        return code == INSTANCE_IS_FINAL || code == INSTANCE_NOT_EXIST;
    }
}
