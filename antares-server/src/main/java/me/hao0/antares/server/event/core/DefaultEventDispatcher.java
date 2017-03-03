package me.hao0.antares.server.event.core;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.util.Systems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * The event dispatcher
 * Author: haolin
 * Email : haolin.h0@gmail.com
 */
@Component
public class DefaultEventDispatcher implements EventDispatcher {

    @Autowired
    private ApplicationContext springContext;

    public final EventBus eventBus;

    public DefaultEventDispatcher() {
        this(Systems.cpuNum() + 1);
    }

    protected DefaultEventDispatcher(Integer threadCount) {
        eventBus = new AsyncEventBus(Executors.newFixedThreadPool(threadCount));
        Logs.info("The event dispatcher has started successfully!");
    }

    @PostConstruct
    public void init(){
        Map<String, EventListener> listeners = springContext.getBeansOfType(EventListener.class);
        if (listeners != null && listeners.size() > 0){
            for (Map.Entry<String, EventListener> listener : listeners.entrySet()){
                Logs.info("The event dispatcher registered the listener({}).", listener);
                register(listener.getValue());
            }
        }
    }

    @Override
    public void register(EventListener listener) {
        eventBus.register(listener);
    }

    @Override
    public void unRegister(EventListener listener) {
        eventBus.unregister(listener);
    }

    @Override
    public void publish(Event event) {
        eventBus.post(event);
    }
}
