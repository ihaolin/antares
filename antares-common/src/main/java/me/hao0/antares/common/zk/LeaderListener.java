package me.hao0.antares.common.zk;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface LeaderListener {

    /**
     * Callback when become the leader
     */
    void isLeader();
}
