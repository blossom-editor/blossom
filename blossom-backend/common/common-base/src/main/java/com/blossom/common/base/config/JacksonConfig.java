package com.blossom.common.base.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.blossom.common.base.util.json.JsonUtil;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Jackson 自定义配置
 */
@Configuration
public class JacksonConfig {

    /**
     * 修改全局的Jackson序列化配置
     */
    @Bean("jackson2ObjectMapperBuilderCustomizer")
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .modules(JsonUtil.getJavaTimeModule())
                // 序列化枚举是以 toString() 来输出，默认 false 。用来处理 Mybatis-plus 通用枚举类的使用
                .featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(Long.TYPE, ToStringSerializer.instance)
                .serializerByType(BigInteger.class, ToStringSerializer.instance)
                .serializerByType(BigDecimal.class, ToStringSerializer.instance);
    }
}
