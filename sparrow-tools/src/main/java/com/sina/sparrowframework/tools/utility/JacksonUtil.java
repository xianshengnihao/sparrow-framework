package com.sina.sparrowframework.tools.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimeZone;

import static com.sina.sparrowframework.tools.utility.DateUtil.TIME_ZONE8;


/**
 * @author tianye6
 * @date 2019/6/18 11:03
 */
public class JacksonUtil {

    public enum ObjectMapperSingleton {
        /**
         *
         */
        MAPPER_FACTORY;

        private ObjectMapper objectMapper;

        ObjectMapperSingleton() {
            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
                @Override
                public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                    jsonGenerator.writeString("");
                }
            });
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.setTimeZone(getDefaultTimeZone());
            objectMapper.registerModule(createJavaTimeModule());
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            simpleModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
            objectMapper.registerModule(simpleModule);
        }

        public ObjectMapper getObjectMapper() {
            return objectMapper;
        }
    }

    public static ObjectMapper getInstance(){
        return ObjectMapperSingleton.MAPPER_FACTORY.getObjectMapper();
    }

    public static <T> T jsonToObject(String jsonString, Class<T> valueType) {
        try {
            return getInstance().readValue(jsonString, valueType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> T byteToObject(byte[] bt, Class<T> valueType) {
        try {
            return getInstance().readValue(new String(bt), valueType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static Map jsonToMap(String jsonString) {
        try {
            return getInstance().readValue(jsonString, Map.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }


    public static <T> T jsonToObject(String jsonStr, TypeReference<T> valueTypeRef) {
        try {
            return getInstance().readValue(jsonStr, valueTypeRef);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static String objectToJson(Object object) {
        try {
            return getInstance().writeValueAsString(object);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static Map objectToMap(Object object) {
        try {
            String jsonString =  getInstance().writeValueAsString(object);
            return getInstance().readValue(jsonString, Map.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static <T> T mapToObject(Map map,Class<T> clz) {
        return getInstance().convertValue(map,clz);
    }

    public static <T> T jsonToObjectList(String json, Class<?> collectionClass, Class<?>... elementClass) {
        JavaType javaType = getInstance().getTypeFactory().constructParametricType(collectionClass, elementClass);
        try {
            return getInstance().readValue(json, javaType);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static JsonNode parseTree(String json) throws IOException {
        try {
            return getInstance().readTree(json);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static JavaTimeModule createJavaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DateUtil.TIME_FORMAT)));

        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DateUtil.TIME_FORMAT)));


        return javaTimeModule;
    }
    public static <T> T readValue(JsonNode node, Class<T> clazz) throws IOException {
        return  getInstance().readValue(node.traverse(), clazz);
    }
    public static TimeZone getDefaultTimeZone() {
        return TIME_ZONE8;
    }
}
