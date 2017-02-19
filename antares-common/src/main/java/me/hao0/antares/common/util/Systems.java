package me.hao0.antares.common.util;

import java.lang.management.ManagementFactory;

public class Systems {

    private static final String PID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    private static final String HOST_PID = Networks.getSiteIp() + ":" + PID;

    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    private Systems() {}

    public static String pid() {
        return PID;
    }

    public static int cpuNum(){
        return CPU_CORES;
    }

    public static String hostPid(){
        return HOST_PID;
    }
}