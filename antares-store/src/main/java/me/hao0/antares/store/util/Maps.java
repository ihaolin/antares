package me.hao0.antares.store.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public final class Maps {

    private static final ObjectMapper mapper = new ObjectMapper();

    private Maps(){}

    /**
     * Serialize an object to map
     * @param object the target object
     * @return the map
     */
    public static Map<?, ?> toMap(Object object){
        return mapper.convertValue(object, Map.class);
    }

    /**
     * Deserialize the map to an object
     * @param fromMap the map
     * @param targetType the object's class
     * @param <T> generic type
     * @return the object
     */
    public static <T> T fromMap(Map<?, ?> fromMap, Class<T> targetType){
        return mapper.convertValue(fromMap, targetType);
    }
}
