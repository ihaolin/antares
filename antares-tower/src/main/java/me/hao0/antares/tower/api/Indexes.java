package me.hao0.antares.tower.api;

import me.hao0.antares.common.dto.JsonResponse;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Controller
public class Indexes extends BasicErrorController {

    public Indexes(ErrorAttributes errorAttributes) {
        super(errorAttributes, new ErrorProperties());
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = super.getStatus(request);

        if (status == HttpStatus.NOT_FOUND) {
            response.setStatus(HttpStatus.OK.value());
            return new ModelAndView("index.html");
        }
        return super.errorHtml(request, response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = super.getStatus(request);
        if (status == HttpStatus.FORBIDDEN) {
            Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
            body.put("message", JsonResponse.AUTH_FAIL.getErr());
            return new ResponseEntity<>(body, status);
        }
        return super.error(request);
    }
}
