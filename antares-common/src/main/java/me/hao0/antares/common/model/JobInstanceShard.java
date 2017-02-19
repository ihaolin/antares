package me.hao0.antares.common.model;

import me.hao0.antares.common.anno.RedisModel;
import java.util.Date;

/**
 * The job instance shard
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RedisModel(prefix = "job_ins_sds")
public class JobInstanceShard implements Model<Long> {

    private static final long serialVersionUID = 4699655089712303564L;

    /**
     * The primary key
     */
    private Long id;

    /**
     * The job instance id
     */
    private Long instanceId;

    /**
     * The shard item index
     */
    private Integer item;

    /**
     * The shard param
     */
    private String param;

    /**
     * The pull client
     */
    private String pullClient;

    /**
     * The finish client
     */
    private String finishClient;

    /**
     * The status
     */
    private Integer status;

    /**
     * The cause when failed
     */
    private String cause;

    /**
     * The pullClient pull time;
     */
    private Date pullTime;

    /**
     * The shard is pulled by total count：
     * <p>
     *     1  : normal <br/>
     *     >1 : has failed execution
     */
    private Integer pullCount;

    /**
     * The pullClient executing start time
     */
    private Date startTime;

    /**
     * The pullClient executing end time
     */
    private Date endTime;

    private Date ctime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
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

    public String getPullClient() {
        return pullClient;
    }

    public void setPullClient(String pullClient) {
        this.pullClient = pullClient;
    }

    public String getFinishClient() {
        return finishClient;
    }

    public void setFinishClient(String finishClient) {
        this.finishClient = finishClient;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCtime() {
        return ctime;
    }

    @Override
    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    @Override
    public void setUtime(Date utime) {
        // ignore
    }

    public Date getPullTime() {
        return pullTime;
    }

    public void setPullTime(Date pullTime) {
        this.pullTime = pullTime;
    }

    public Integer getPullCount() {
        return pullCount;
    }

    public void setPullCount(Integer pullCount) {
        this.pullCount = pullCount;
    }

    @Override
    public String toString() {
        return "JobInstanceShard{" +
                "id=" + id +
                ", instanceId=" + instanceId +
                ", item=" + item +
                ", param='" + param + '\'' +
                ", pullClient='" + pullClient + '\'' +
                ", finishClient='" + finishClient + '\'' +
                ", status=" + status +
                ", cause='" + cause + '\'' +
                ", pullTime=" + pullTime +
                ", pullCount=" + pullCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", ctime=" + ctime +
                '}';
    }
}
