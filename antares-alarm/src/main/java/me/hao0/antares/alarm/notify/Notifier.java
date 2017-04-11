package me.hao0.antares.alarm.notify;

import me.hao0.antares.alarm.alarmer.AlarmContext;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface Notifier {

    Boolean notify(AlarmContext context);
}
