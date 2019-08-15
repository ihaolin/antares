package me.hao0.antares.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author backflow
 * Created on 2019-06-13 14:26
 */
public class Jacksons {

    private static final Logger LOG = LoggerFactory.getLogger(Jacksons.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<HashMap> mapTypeReference = new TypeReference<HashMap>() {};

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
    }

    /**
     * Object to JSON string
     *
     * @param o target object
     * @return JSON string
     */
    public static String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (IOException e) {
            LOG.warn("To JSON string error", e);
        }
        return "";
    }

    /**
     * Serialize an object to map
     *
     * @param object the target object
     * @return the map
     */
    public static Map<String, Object> toMap(Object object) {
        return mapper.convertValue(object, mapTypeReference);
    }

    /**
     * Deerialize an string to map
     *
     * @param content the string content
     * @return the map
     */
    public static Map<String, Object> toMap(String content) {
        try {
            return mapper.readValue(content, mapTypeReference);
        } catch (IOException e) {
            LOG.warn("Object to Map error", e);
            return Collections.emptyMap();
        }
    }

    /**
     * Deserialize the map to an object
     *
     * @param fromMap    the map
     * @param targetType the object's class
     * @param <T>        generic type
     * @return the object
     */
    public static <T> T fromMap(Map<?, ?> fromMap, Class<T> targetType) {
        return mapper.convertValue(fromMap, targetType);
    }

    /**
     * Deserialize the JSON string to target class instance
     *
     * @param json  JSON string
     * @param clazz target class
     * @return the instance
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().length() == 0) {
            return null;
        }

        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            LOG.warn("fromJson error: " + json, e);
            return null;
        }
    }

    /**
     * Convert object to target class
     *
     * @param object Source object
     * @param clazz  Target class
     * @return Target class instance
     */
    public static <T> T convert(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }

        return mapper.convertValue(object, clazz);
    }
}
