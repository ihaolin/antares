package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ClientInfo implements Serializable {

    private static final long serialVersionUID = -3395261718901387010L;

    private String addr;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "addr='" + addr + '\'' +
                '}';
    }
}

