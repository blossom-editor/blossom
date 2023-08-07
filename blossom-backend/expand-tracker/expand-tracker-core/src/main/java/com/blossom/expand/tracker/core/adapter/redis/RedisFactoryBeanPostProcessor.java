package com.blossom.expand.tracker.core.adapter.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.blossom.common.base.util.ProxyUtils;
import com.blossom.common.base.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Slf4j
@SuppressWarnings("all")
public class RedisFactoryBeanPostProcessor implements BeanPostProcessor {

    private static final String REDIS_CONNECTION_FACTORY_BEAN_NAME = "redisConnectionFactory";

    private TrackerRedisProperties trackerRedisProperties;

    public RedisFactoryBeanPostProcessor(TrackerRedisProperties trackerRedisProperties) {
        this.trackerRedisProperties = trackerRedisProperties;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (REDIS_CONNECTION_FACTORY_BEAN_NAME.equals(beanName)) {
            log.info("[TRACKERS] 已经适配框架 : Redis");
            // 使用日志代理，覆盖原 Redis 工厂类
            return ProxyUtils.getProxy(bean, invocation -> {
                return new RedisConnectionFactoryProxy(trackerRedisProperties).interceptorRedisFactory(invocation);
            });
        } else if (bean instanceof RedisTemplate) {
            // 避免默认的序列化方式，导致进入Redis的字符串无法直接辨识
            initSerializer((RedisTemplate) bean);
        }
        return bean;
    }

    private static void initSerializer(RedisTemplate redisTemplate) {
        // Key用StringRedisSerializer，避免写入Redis的Key和Value，前缀都会出现 \xAC\xED\x00\x05t\x00\x03
//        RedisSerializer keySerializer = new StringRedisSerializer();
//        redisTemplate.setKeySerializer(keySerializer);
//        redisTemplate.setHashKeySerializer(keySerializer);
//        RedisJsonValueSerializer valSerializer = new RedisJsonValueSerializer(Object.class);
//        redisTemplate.setValueSerializer(valSerializer);
//        redisTemplate.setHashValueSerializer(valSerializer);
    }

    @Slf4j
    private static class RedisJsonValueSerializer<T> implements RedisSerializer<T> {

        protected static ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);

        private Class<T> clazz;

        public RedisJsonValueSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) {
            return JsonUtil.toByte(t);
//            try {
//                return mapper.writeValueAsBytes(t);
//            } catch (JsonProcessingException e) {
//                log.error(e.toString());
//                return null;
//            }
        }

        @Override
        public T deserialize(byte[] bytes) {
            if (bytes == null) {
                return null;
            }
            return JsonUtil.toObj(bytes, clazz);
//          return mapper.readValue(bytes, clazz);
        }

    }
}
