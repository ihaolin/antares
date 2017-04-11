package me.hao0.antares.alarm.notify;

import com.google.common.collect.Maps;
import me.hao0.antares.alarm.alarmer.AlarmContext;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.enums.AlarmNotifyType;
import me.hao0.antares.common.util.CollectionUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class DefaultNotifierManager implements NotifierManager, InitializingBean {

    private final Map<AlarmNotifyType, Notifier> notifiers = Maps.newHashMap();

    @Autowired
    private ApplicationContext springContext;

    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String, Notifier> notifierMap = springContext.getBeansOfType(Notifier.class);

        if (!CollectionUtil.isNullOrEmpty(notifierMap)){
            NotifierMeta notifyMeta;
            for (Notifier notifier : notifierMap.values()){
                notifyMeta = notifier.getClass().getAnnotation(NotifierMeta.class);
                if (notifyMeta != null){
                    notifiers.put(notifyMeta.type(), notifier);
                    Logs.info("Discovered the alarm notifier({}).", notifier);
                }
            }
        }

    }

    @Override
    public Boolean notify(AlarmNotifyType type, AlarmContext context) {
        return notifiers.get(type).notify(context);
    }
}
