package me.hao0.antares.tower.interceptor;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import me.hao0.antares.common.log.Logs;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class LocaleInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver == null) {
            return true;
        }

        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null){
                for (Cookie cookie : cookies){
                    if (Objects.equal("lang", cookie.getName())){
                        LocaleContextHolder.setLocale(new Locale(cookie.getValue()));
                    }
                }
            }
        } catch (Exception e) {
            Logs.error("occur errors when resolve locale: {}", Throwables.getStackTraceAsString(e));
        }

        return true;
    }
}
