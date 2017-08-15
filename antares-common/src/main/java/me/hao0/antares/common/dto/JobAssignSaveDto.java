package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * Author : haolin
 * Email  : haolin.h0@gmail.com
 */
public class JobAssignSaveDto implements Serializable {

    private static final long serialVersionUID = 4263372541949069063L;

    private String assignIps;

    public String getAssignIps() {
        return assignIps;
    }

    public void setAssignIps(String assignIps) {
        this.assignIps = assignIps;
    }

    @Override
    public String toString() {
        return "JobAssignSaveDto{" +
                "assignIps='" + assignIps + '\'' +
                '}';
    }
}
