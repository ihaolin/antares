package me.hao0.antares.common.dto;

import me.hao0.antares.common.model.enums.JobInstanceStatus;
import java.io.Serializable;

/**
 * The job instance dto
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobInstanceDto implements Serializable {

    private static final long serialVersionUID = 119889051080239175L;

    /**
     * The instance id
     */
    private Long id;

    /**
     * The job id
     */
    private Long jobId;

    /**
     * The job instance status
     * @see JobInstanceStatus
     */
    private Integer status;

    /**
     * The status desc
     */
    private String statusDesc;

    /**
     * The server, scheduling this job
     */
    private String server;

    /**
     * The instance execution start time
     */
    private String startTime;

    /**
     * The instance execution end time
     */
    private String endTime;

    /**
     * The cost time:
     * <p>
     *     endTime - startTime
     * </p>
     */
    private String costTime;

    /**
     * The cause when failed
     */
    private String cause;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "JobInstanceDto{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", status=" + status +
                ", statusDesc='" + statusDesc + '\'' +
                ", server='" + server + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", cause='" + cause + '\'' +
                '}';
    }
}
