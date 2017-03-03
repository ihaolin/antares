package me.hao0.antares.server.event.job;

import com.google.common.eventbus.Subscribe;
import me.hao0.antares.server.event.core.EventListener;
import org.springframework.stereotype.Component;

/**
 * The job event subscriber
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class JobEventListener implements EventListener {



    @Subscribe
    public void onJobFinished(JobFinishedEvent e){

    }
}
