package me.hao0.antares.common.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * Job assign dto
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
public class JobAssignDto implements Serializable {

    private static final long serialVersionUID = 4178691834316148023L;

    /**
     * The client ip
     */
    private String ip;

    /**
     * Is assigned or not
     */
    private Boolean assign;

    /**
     * The processes of the client ip
     */
    private Set<String> processes;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getAssign() {
        return assign;
    }

    public void setAssign(Boolean assign) {
        this.assign = assign;
    }

    public Set<String> getProcesses() {
        return processes;
    }

    public void setProcesses(Set<String> processes) {
        this.processes = processes;
    }

    @Override
    public String toString() {
        return "JobAssignDto{" +
                "ip='" + ip + '\'' +
                ", assign=" + assign +
                ", processes=" + processes +
                '}';
    }
}
