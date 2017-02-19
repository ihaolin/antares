package me.hao0.antares.store.exception;

import me.hao0.antares.common.model.enums.ShardOperateRespCode;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class ShardOperateException extends RuntimeException {

    private ShardOperateRespCode code;

    public ShardOperateException(ShardOperateRespCode code) {
        this.code = code;
    }

    public ShardOperateRespCode getCode() {
        return code;
    }
}
