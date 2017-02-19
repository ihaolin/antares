package me.hao0.antares.common.dto;

import java.io.Serializable;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class JobFireTime implements Serializable {

    private static final long serialVersionUID = 4612715888992171290L;

    /**
     * The current fire time
     */
    private String current;

    /**
     * The previous fire time
     */
    private String prev;

    /**
     * The next fire time
     */
    private String next;

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "JobFireTime{" +
                "current='" + current + '\'' +
                ", prev='" + prev + '\'' +
                ", next='" + next + '\'' +
                '}';
    }
}
