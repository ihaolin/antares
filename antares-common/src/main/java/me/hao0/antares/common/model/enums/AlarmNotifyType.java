package me.hao0.antares.common.model.enums;

import java.util.Objects;

/**
 * The alarm notify type
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public enum AlarmNotifyType {

    /**
     * The email notify
     */
    EMAIL(1, "EMAIL"),

    /**
     * The sms notify
     */
    SMS(2, "SMS"),

    /**
     * The mobile notify
     */
    MOBILE(4, "MOBILE"),

    /**
     * The wechat notify
     */
    WECHAT(8, "WECHAT");

    private Integer value;

    private String code;

    AlarmNotifyType(Integer value, String code){
        this.value = value;
        this.code = code;
    }

    public Integer value(){
        return value;
    }

    public String code(){
        return code;
    }

    public static AlarmNotifyType from(Integer value){

        for (AlarmNotifyType t : AlarmNotifyType.values()){
            if (Objects.equals(t.value, value)){
                return t;
            }
        }

        throw new IllegalStateException("invalid alarm notify type: " + value);
    }
}
