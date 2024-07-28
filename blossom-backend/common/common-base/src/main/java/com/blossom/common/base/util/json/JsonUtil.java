package com.blossom.common.base.util.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * JSON工具类
 *
 * @author : xzzz
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    static {
        SimpleDateFormat myDateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        mapper.setDateFormat(myDateFormat);
        // 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许下换线和驼峰之间的转换
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        // 允许出现单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 解决：序列化 map 时，有 key 为 null 的情况，jackson序列化会报 Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)
        mapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer());
        mapper.registerModule(getJavaTimeModule());
        // 忽视为null的字段
//        mapper.setSerializationInclusion(Include.NON_NULL);
    }


    /**
     * 返回一个可以对任何属性进行序列化, 且在 Json 中会携带类型的 ObjectMapper
     *
     * @return ObjectMapper
     */
    public static ObjectMapper typeObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        /*
         * 所有字段都会被序列化, 无论这个字段是否有 getter/setter 方法, 以及方法是否是 public 或其他
         */
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        /*
         * 将在 Json 中带有被序列化的类型, 用于反序列化时的类型推断
         */
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        objectMapper.registerModule(getJavaTimeModule());
        return objectMapper;
    }

    /**
     * Key为null是的序列化方式
     */
    public static class NullKeySerializer extends StdSerializer<Object> {

        public NullKeySerializer() {
            this(null);
        }

        public NullKeySerializer(Class<Object> t) {
            super(t);
        }

        @Override
        public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused) throws IOException {
            jsonGenerator.writeFieldName("null");
        }
    }

    /**
     * LocalDateTime系列序列化和反序列化模块，继承自jsr310
     */
    public static Module getJavaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        return javaTimeModule;
    }


    /**
     * 判断json格式是否合法
     *
     * @param str 字符串
     * @return true:为Json,false:不为Json
     */
    public static boolean isJson(String str) {
        try {
            mapper.readTree(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    // region 字符串转其他


    /**
     * 字符串转对象
     */
    public static <T> T toObj(String str, Class<T> c) {
        if (StrUtil.isBlank(str)) {
            return null;
        }
        T t = null;
        try {
            t = mapper.readValue(str, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * JSON字符串转为指定的类
     *
     * @param str 字符串
     * @param tr  指定类
     */
    public static <T> T toObj(String str, TypeReference<T> tr) {
        if (StrUtil.isBlank(str)) {
            return null;
        }

        T t = null;
        try {
            t = (T) mapper.readValue(str, tr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) t;
    }

    /**
     * 将JSON转为MAP
     */
    public static Map toMap(String str) {
        try {
            return mapper.readValue(str, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("转换错误:" + e.getMessage());
        }
    }

    public static Map toMap(Object str) {
        try {
            return mapper.readValue(toJson(str), HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("转换错误:" + e.getMessage());
        }
    }

    /**
     * json字符串转JsonNode
     */
    public static JsonNode toJsonNode(String jsonStr) {
        try {
            return mapper.readTree(jsonStr);
        } catch (Exception e) {
            throw new IllegalStateException("转换错误:" + e.getMessage());
        }
    }

    /**
     * json字符串集合转对象集合
     *
     * @param strings 字符串集合
     * @param clazz   对象类型
     * @param <E>     对象泛型
     * @return 对象集合
     */
    public static <E> List<E> toObjList(Collection<String> strings, Class<E> clazz) {
        if (CollUtil.isEmpty(strings)) {
            return new ArrayList<>();
        }
        List<E> list = new ArrayList<>();
        for (String string : strings) {
            E e = toObj(string, clazz);
            list.add(e);
        }

        return list;
    }
    // endregion


    // region 对象转其他

    /**
     * 对象转为JSON
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        String s;
        try {
            s = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("对象无法转换为JSON:" + e.getMessage());
        }
        return s;
    }

    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        String s;
        try {
            s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("对象无法转换为JSON:" + e.getMessage());
        }
        return s;
    }

    /**
     * 对象转JsonNode
     */
    public static JsonNode toJsonNode(Object obj) {
        try {
            return mapper.readTree(toJson(obj));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 对象转byte数组
     */
    public static byte[] toByte(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    // endregion


    // region Map转其他

    /**
     * 将Map转成指定的Bean
     */
    public static <T> T toObj(Map<?, ?> map, Class<T> clazz) {
        try {
            return mapper.readValue(toJson(map), clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // endregion


    // region Byte数组转其他


    /**
     * 数组转JsonNode
     */
    public static JsonNode toJsonNode(byte[] bytes) {
        try {
            return mapper.readValue(bytes, JsonNode.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 数组转JsonNode
     */
    public static Object toObj(byte[] bytes) {
        try {
            return mapper.readValue(bytes, Object.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T toObj(byte[] bytes, Class<T> clazz) {
        try {
            return mapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * byte数组转Json
     */
    public static String toJson(byte[] bytes) {
        return new String(bytes);
    }
    // endregion


    // region JsonNode 转其他

    /**
     * JsonNode转byte[]
     */
    public static byte[] toByte(JsonNode node) {
        try {
            return mapper.writeValueAsBytes(node);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    // endregion

}
