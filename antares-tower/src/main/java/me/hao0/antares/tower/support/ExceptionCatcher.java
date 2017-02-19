package me.hao0.antares.tower.support;

import com.google.common.base.Throwables;
import me.hao0.antares.common.dto.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author: haolin
 * Date:   8/25/16
 * Email:  haolin.h0@gmail.com
 */
@ControllerAdvice
public class ExceptionCatcher {

    private Logger log = LoggerFactory.getLogger(ExceptionCatcher.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public JsonResponse paramMissing(MissingServletRequestParameterException e) {
        log.warn("client params is missing: {}", Throwables.getStackTraceAsString(e));
        return JsonResponse.PARAM_MISSING;
    }

}
