package me.hao0.antares.common.model.enums;

/**
 * Job instance record type
 *
 * @author backflow
 * Created on 2019-06-13 14:26
 */
public enum RecordType {

    /**
     * Record all job instances
     */
    ALL,

    /**
     * Record only succeed executed instance
     */
    ONLY_SUCCEED,

    /**
     * Record only failed executed instance
     */
    ONLY_FAILD

}
