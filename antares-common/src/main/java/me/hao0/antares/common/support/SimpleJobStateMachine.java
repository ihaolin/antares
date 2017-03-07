package me.hao0.antares.common.support;

import me.hao0.antares.common.model.enums.JobState;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple job state machine
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class SimpleJobStateMachine {

    /**
     * state set
     * <p>
     *     A state : the allowable previous state set
     * </p>
     */
    private final Map<JobState, Set<JobState>> states = new HashMap<>();

    private SimpleJobStateMachine(){

        configure(JobState.WAITING, JobState.RUNNING);

        configure(JobState.PAUSED, JobState.WAITING);
        configure(JobState.RUNNING, JobState.WAITING);

        configure(JobState.WAITING, JobState.PAUSED);
        configure(JobState.RUNNING, JobState.PAUSED);
        configure(JobState.FAILED, JobState.PAUSED);

        configure(JobState.PAUSED, JobState.STOPPED);
        configure(JobState.WAITING, JobState.STOPPED);
        configure(JobState.RUNNING, JobState.STOPPED);
        configure(JobState.FAILED, JobState.STOPPED);
        configure(JobState.STOPPED, JobState.STOPPED);

        // may be failed when update the job to waiting after finish job
        configure(JobState.WAITING, JobState.FAILED);
        configure(JobState.RUNNING, JobState.FAILED);

    }

    /**
     * Configure a state transfer
     * @param prev the previous state
     * @param next the next state
     * <p>
     *    prev -> next
     * </p>
     */
    private void configure(JobState prev, JobState next){
        Set<JobState> previousStates = states.get(next);
        if (previousStates == null){
            previousStates = new HashSet<>();
            states.put(next, previousStates);
        }
        previousStates.add(prev);
    }

    /**
     * Allow to transfer
     * @param current the current state
     * @param target the target state
     * @return return true if allow, or false
     */
    public Boolean allow(JobState current, JobState target){
        Set<JobState> allows = states.get(target);
        if (allows == null || allows.isEmpty()){
            return Boolean.FALSE;
        }
        return allows.contains(current);
    }

    private static class SimpleJobStateMachineHolder{
        static SimpleJobStateMachine INSTANCE = new SimpleJobStateMachine();
    }

    public static SimpleJobStateMachine get(){
        return SimpleJobStateMachineHolder.INSTANCE;
    }


}
