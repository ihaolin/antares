package me.hao0.antares.server.event.core;

/**
 * The event dispatcher
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface EventDispatcher {

    /**
     * Add the subscriber
     * @param listener the event subscriber
     */
    void register(EventListener listener);

    /**
     * Remove the subscriber
     * @param listener the subscriber
     */
    void unRegister(EventListener listener);

    /**
     * Publish the event
     * @param event the event
     */
    void publish(Event event);
}
