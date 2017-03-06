package me.hao0.antares.tower.support;

import me.hao0.antares.common.dto.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@ControllerAdvice
public class ExceptionCatcher {

    @Autowired
    private Messages messages;

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public JsonResponse paramMissing() {
        String errMsg = messages.get(JsonResponse.PARAM_MISSING.getErr().toString());
        return JsonResponse.notOk(errMsg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public JsonResponse paramFormatError(){
        String errMsg = messages.get(JsonResponse.PARAM_FORMAT_ERROR.getErr().toString());
        return JsonResponse.notOk(errMsg);
    }

}
