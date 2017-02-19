package me.hao0.antares.tower.api;

import com.alibaba.fastjson.JSON;
import me.hao0.antares.common.dto.JsonResponse;
import me.hao0.antares.tower.util.Responses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Controller
public class Indexes extends AbstractErrorController {

    @Autowired
    public Indexes(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @RequestMapping(value = {"/", "/index", "/index.html"})
    public String index(){
        return "index";
    }

    @RequestMapping("/error")
    public String error(HttpServletRequest request, HttpServletResponse response){

        HttpStatus status = super.getStatus(request);

        if (status == HttpStatus.NOT_FOUND){
            response.setStatus(HttpStatus.OK.value());
            return "index";
        }

        if (status == HttpStatus.FORBIDDEN){
            Responses.writeJson(response, JSON.toJSONString(JsonResponse.AUTH_FAIL));
            return "";
        }

        // default index
        return "index";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
