package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AppDeleteDto implements Serializable {

    private static final long serialVersionUID = 7508627524604536152L;

    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "AppDeleteDto{" +
                "appName='" + appName + '\'' +
                '}';
    }
}
