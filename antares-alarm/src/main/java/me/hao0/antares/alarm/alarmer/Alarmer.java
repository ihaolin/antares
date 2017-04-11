package me.hao0.antares.alarm.alarmer;

import me.hao0.antares.common.model.AlarmEvent;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface Alarmer {

    /**
     * Filter the alarm event
     * @param e the alarm event
     * @return return true if need to be alarm, or false
     */
    Boolean filter(AlarmEvent e);

    /**
     * Send the alarm
     * @param e the alarm event
     * @return return true alarm successfully, or false
     */
    Boolean alarm(AlarmEvent e);
}
