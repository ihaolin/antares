package me.hao0.antares.tower.api;

import me.hao0.antares.common.dto.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RestController
@RequestMapping("/api/users")
public class Users {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @RequestMapping("/logout")
    public JsonResponse logout() throws Exception {
        return JsonResponse.AUTH_FAIL;
    }

}


