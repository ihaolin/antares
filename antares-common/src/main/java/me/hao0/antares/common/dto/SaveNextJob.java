package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * The next job dto
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SaveNextJob implements Serializable {

    private static final long serialVersionUID = 299727901934803683L;

    /**
     * The job id
     */
    private Long nextJobId;

    public Long getNextJobId() {
        return nextJobId;
    }

    public void setNextJobId(Long nextJobId) {
        this.nextJobId = nextJobId;
    }

    @Override
    public String toString() {
        return "SaveNextJob{" +
                "nextJobId=" + nextJobId +
                '}';
    }
}
