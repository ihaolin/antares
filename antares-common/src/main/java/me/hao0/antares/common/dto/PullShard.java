package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class PullShard implements Serializable {

    private static final long serialVersionUID = -7899746031432616077L;

    /**
     * The shard id
     */
    private Long id;

    /**
     * The shard item index
     */
    private Integer item;

    /**
     * The shard param
     */
    private String param;

    /**
     * The job param
     */
    private String jobParam;

    /**
     * The total shard count
     */
    private Integer totalShardCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getJobParam() {
        return jobParam;
    }

    public void setJobParam(String jobParam) {
        this.jobParam = jobParam;
    }

    public Integer getTotalShardCount() {
        return totalShardCount;
    }

    public void setTotalShardCount(Integer totalShardCount) {
        this.totalShardCount = totalShardCount;
    }

    @Override
    public String toString() {
        return "PullShard{" +
                "id=" + id +
                ", item=" + item +
                ", param='" + param + '\'' +
                ", jobParam='" + jobParam + '\'' +
                ", totalShardCount=" + totalShardCount +
                '}';
    }
}
