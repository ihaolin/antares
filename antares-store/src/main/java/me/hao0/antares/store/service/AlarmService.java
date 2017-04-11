package me.hao0.antares.store.service;

import me.hao0.antares.common.model.AlarmEvent;
import me.hao0.antares.common.util.Response;
import java.util.List;

/**
 * The alarm service
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface AlarmService {

    /**
     * Push an alarm event
     * @param event the event
     * @return return true if push successfully, or false
     */
    Response<Boolean> push(AlarmEvent event);

    /**
     * Pull the alarm event
     * @param size the batch size
     * @return the alarm event data list
     */
    Response<List<AlarmEvent>> pull(Integer size);
}
