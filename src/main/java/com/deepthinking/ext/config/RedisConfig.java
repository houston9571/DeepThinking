package com.deepthinking.ext.config;

import com.deepthinking.ext.serialize.FastJson2RedisSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@AutoConfigureAfter({RedisAutoConfiguration.class})
public class RedisConfig {


    @Bean
    @ConditionalOnMissingBean(name = {"redisTemplate"})
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        FastJson2RedisSerializer<Object> fastjsonRedisSerializer = new FastJson2RedisSerializer<>(Object.class);
        template.setStringSerializer(RedisSerializer.string());
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(fastjsonRedisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(fastjsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

//    @Bean
//    @ConditionalOnMissingBean({StringRedisTemplate.class})
//    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(redisConnectionFactory);
//        return template;
//    }

}
