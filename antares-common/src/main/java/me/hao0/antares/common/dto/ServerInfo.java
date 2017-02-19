package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ServerInfo implements Serializable {

    private static final long serialVersionUID = 5143623538738976199L;

    /**
     * The server host
     */
    private String server;

    /**
     * Is the leader or not
     */
    private Boolean leader;

    /**
     * The scheduling jobs count
     */
    private Integer jobCount;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Boolean getLeader() {
        return leader;
    }

    public void setLeader(Boolean leader) {
        this.leader = leader;
    }

    public Integer getJobCount() {
        return jobCount;
    }

    public void setJobCount(Integer jobCount) {
        this.jobCount = jobCount;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "server='" + server + '\'' +
                ", leader=" + leader +
                ", jobCount=" + jobCount +
                '}';
    }
}
