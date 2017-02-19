package me.hao0.antares.server.api;

import me.hao0.antares.server.cluster.server.ServerHost;
import me.hao0.antares.server.schedule.JobPool;
import me.hao0.antares.store.service.JobService;
import me.hao0.antares.store.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import static me.hao0.antares.store.util.ServerUris.*;

/**
 * The server api
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping(value = SERVERS)
public class Servers {

    @Autowired
    private JobPool jobPool;

    @Autowired
    private JobService jobService;

    @Autowired
    private ServerHost host;

    @RequestMapping(value = JOB_SCHEDULE + "/{jobId}", method = RequestMethod.POST)
    public Boolean scheduleJob(@PathVariable(value = "jobId") Long jobId){

        Response<Boolean> bindResp = jobService.bindJob2Server(jobId, host.get());
        if (!bindResp.isSuccess()){
            return Boolean.FALSE;
        }

        // start to schedule the job
        return jobPool.scheduleJob(jobId);
    }

    /**
     * Trigger the job
     * @param jobId the job id
     * @return return true if trigger successfully, or false
     */
    @RequestMapping(value = JOB_TRIGGER + "/{jobId}", method = RequestMethod.POST)
    public Boolean triggerJob(@PathVariable(value = "jobId") Long jobId){
        return jobPool.triggerJob(jobId);
    }

    /**
     * Pause the job
     * @param jobId the job id
     * @return return true if pause successfully, or false
     */
    @RequestMapping(value = JOB_PAUSE + "/{jobId}", method = RequestMethod.POST)
    public Boolean pauseJob(@PathVariable(value = "jobId") Long jobId){
        return jobPool.pauseJob(jobId);
    }

    /**
     * Resume the job
     * @param jobId the job id
     * @return return true if resume successfully, or false
     */
    @RequestMapping(value = JOB_RESUME + "/{jobId}", method = RequestMethod.POST)
    public Boolean resumeJob(@PathVariable(value = "jobId") Long jobId){
        return jobPool.resumeJob(jobId);
    }

    /**
     * Remove the job
     * @param jobId the job id
     * @return return true if remove successfully, or false
     */
    @RequestMapping(value = JOB_REMOVE + "/{jobId}", method = RequestMethod.POST)
    public Boolean removeJob(@PathVariable(value = "jobId") Long jobId){
        return jobPool.removeJob(jobId);
    }

    /**
     * Reload the job scheduling
     * @param jobId the job id
     * @return return true if remove successfully, or false
     */
    @RequestMapping(value = JOB_RELOAD + "/{jobId}", method = RequestMethod.POST)
    public Boolean reloadJob(@PathVariable(value = "jobId") Long jobId){
        return jobPool.reloadJob(jobId);
    }
}
