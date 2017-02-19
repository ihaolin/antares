package me.hao0.antares.server.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class Springs {

    @Autowired
    private ApplicationContext springContext;

    public <T> T getBean(Class<T> beanClass){
        return springContext.getBean(beanClass);
    }
}
