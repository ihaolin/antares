package me.hao0.antares.common.exception;

import me.hao0.antares.common.model.enums.JobState;

/**
 * @author haolin
 * @mailto haolin.h0@gmail.com
 */
public class JobStateTransferInvalidException extends RuntimeException {

    private String id;

    private JobState current;

    private JobState target;

    public JobStateTransferInvalidException(String id, JobState current, JobState target){
        super();
        this.id = id;
        this.current = current;
        this.target = target;
    }

    @Override
    public String toString() {
        return "JOB(" + id + "): " + current + " --> " + target;
    }
}
