package me.hao0.antares.store.dao;

import me.hao0.antares.common.model.AlarmEvent;

import java.util.List;

/**
 * The alarm event dao
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface AlarmEventDao extends BaseDao<AlarmEvent> {

    /**
     * Push the alarm event to the queue
     * @param id the event id
     * @return return true if push successfully
     */
    Boolean push(Long id);

    /**
     * Pull the alarm event from the queue
     * @param size the pull size
     * @return alarm event id list
     */
    List<Long> pull(Integer size);
}
