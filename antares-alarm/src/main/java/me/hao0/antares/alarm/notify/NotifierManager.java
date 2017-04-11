package me.hao0.antares.alarm.notify;

import me.hao0.antares.alarm.alarmer.AlarmContext;
import me.hao0.antares.common.model.enums.AlarmNotifyType;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface NotifierManager {

    Boolean notify(AlarmNotifyType type, AlarmContext context);
}
