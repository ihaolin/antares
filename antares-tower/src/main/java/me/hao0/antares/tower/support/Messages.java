package me.hao0.antares.tower.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Component
public class Messages {

    @Autowired
    private MessageSource messageSource;

    public String get(Object key){
        return get(String.valueOf(key));
    }

    public String get(String key){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, null, locale);
    }

    public String get(String key, Object[] args){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }
}
