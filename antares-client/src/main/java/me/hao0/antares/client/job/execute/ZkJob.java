package me.hao0.antares.client.job.execute;

import com.google.common.base.Strings;
import me.hao0.antares.client.core.AntaresClient;
import me.hao0.antares.client.job.Job;
import me.hao0.antares.common.support.Lifecycle;
import me.hao0.antares.common.support.Component;
import me.hao0.antares.common.util.ZkPaths;
import me.hao0.antares.common.zk.ChildListener;
import me.hao0.antares.common.zk.ChildWatcher;

public class ZkJob extends Component implements Lifecycle {

    /**
     * The job implements
     */
    private Job job;

    /**
     * The watcher
     */
    private ChildWatcher watcher;

    private final AntaresClient client;

    public ZkJob(AntaresClient client, Job job) {
        this.client = client;
        this.job = job;
    }

    @Override
    public void doStart() {

        String appName = client.getAppName();

        String jobClass = getJobClass();

        String jobInstancesNodePath = ZkPaths.pathOfJobInstances(appName, jobClass);
        this.watcher = client.getZk().newChildWatcher(jobInstancesNodePath, new ChildListener() {
            @Override
            protected void onAdd(String path, byte[] data) {
                // fired a new job instance

                String instanceId = ZkPaths.lastNode(path);

                if (Strings.isNullOrEmpty(instanceId)) return;

                // execute the job
                client.getJobExecutor().execute(Long.valueOf(instanceId), ZkJob.this);
            }
        });
    }

    public Job getJob() {
        return job;
    }

    public String getJobClass(){
        return job.getClass().getName();
    }

    @Override
    public void doShutdown() {
        watcher.stop();
    }
}