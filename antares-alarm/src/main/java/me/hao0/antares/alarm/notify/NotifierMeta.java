package me.hao0.antares.alarm.notify;

import me.hao0.antares.common.model.enums.AlarmNotifyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NotifierMeta {

    /**
     * The alarm notify type
     */
    AlarmNotifyType type();
}
