package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AppSaveDto implements Serializable {

    private static final long serialVersionUID = -4153657694380103021L;

    private String appName;

    private String appKey;

    private String appDesc;

    private Long inheritAppId;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public Long getInheritAppId() {
        return inheritAppId;
    }

    public void setInheritAppId(Long inheritAppId) {
        this.inheritAppId = inheritAppId;
    }

    @Override
    public String toString() {
        return "AppSaveDto{" +
                "appName='" + appName + '\'' +
                ", appKey='" + appKey + '\'' +
                ", appDesc='" + appDesc + '\'' +
                ", inheritAppId=" + inheritAppId +
                '}';
    }
}
