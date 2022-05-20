package com.hd123.baas.sop.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.hd123.rumba.commons.json.JsonArray;
import com.hd123.rumba.commons.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static ObjectMapper mapper;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleDateFormat fmt = new SimpleDateFormat(DATE_FORMAT);
        mapper.setDateFormat(fmt);
        AnnotationIntrospector aiJaxb = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
        AnnotationIntrospector aiJackson = new JacksonAnnotationIntrospector();
        mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(aiJaxb, aiJackson));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static String objectToJson(Object value) throws RuntimeException {
        if (value == null)
            return null;

        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T jsonToObject(String json, Class<T> valueType) {
        if (StringUtils.isBlank(json))
            return null;

        try {
            return mapper.readValue(json, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> List<T> jsonToList(String json, Class<T> valueType) {
        if (StringUtils.isBlank(json))
            return null;

        List<T> result = new ArrayList<>();
        try {
            JsonArray array = new JsonArray(json);
            for (int i = 0; i < array.length(); i++) {

                if (valueType == String.class) {
                    T jsonObject = (T) array.getString(i);
                    result.add(jsonObject);
                } else {
                    JsonObject jsonObject = array.getJsonObject(i);
                    T object = jsonToObject(jsonObject.toString(), valueType);
                    result.add(object);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T convert(Object obj, Class<T> valueType) {
        String json = objectToJson(obj);

        if (StringUtils.isBlank(json))
            return null;

        try {
            return mapper.readValue(json, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
