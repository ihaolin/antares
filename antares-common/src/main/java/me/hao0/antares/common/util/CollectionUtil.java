package me.hao0.antares.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public final class CollectionUtil {

    public static boolean isNullOrEmpty(Collection c){
        return c == null || c.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> m){
        return m == null || m.isEmpty();
    }
}
