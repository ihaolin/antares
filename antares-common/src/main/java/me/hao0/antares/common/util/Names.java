package me.hao0.antares.common.util;

import com.google.common.base.CaseFormat;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class Names {

    public static String toCamel(String origin){
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, origin);
    }

    public static String toUnderScore(String origin){
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, origin);
    }
}
