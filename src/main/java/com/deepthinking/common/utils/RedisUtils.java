package com.deepthinking.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.deepthinking.common.constant.Constants.THREE_MINUTES;

@Slf4j
public class RedisUtils {

    private static RedisTemplate<String, Object> redisTemplate;
    private static ValueOperations<String, Object> stringOps;
    private static HashOperations<String, String, Object> hashOps;
    private static ListOperations<String, Object> listOps;
    private static SetOperations<String, Object> setOps;
    private static ZSetOperations<String, Object> zsetOps;


    static {
        redisTemplate = SpringContextUtils.getBean("redisTemplate", RedisTemplate.class);
        stringOps = redisTemplate.opsForValue();    //操作字符串
        hashOps = redisTemplate.opsForHash();       //操作hash
        listOps = redisTemplate.opsForList();       //操作list
        setOps = redisTemplate.opsForSet();         //操作set
        zsetOps = redisTemplate.opsForZSet();       //操作有序set
        log.info("################   RedisUtils init finish. redisTemplate:{}   ################", redisTemplate.hashCode());
    }

    /**
     * @param key  键
     * @param time 时间(秒)
     * @description: 指定缓存失效时间
     */
    public static void expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("class:RedisUtils method:expire() => Exception {}", e.getMessage());
        }
    }

    public static void expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception e) {
            log.error("class:RedisUtils method:expire() => Exception {}", e.getMessage());
        }
    }

    /**
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     * @description: 根据key 获取过期时间
     */
    public static Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 移除key的过期时间，将key永久保存
     *
     * @param key
     */
    public void persist(String key) {
        redisTemplate.persist(key);
    }

    /**
     * @param key 键
     * @return true 存在 false不存在
     * @description: 判断key是否存在
     */
    public static boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("class:RedisUtils method:exists() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * @param key 可以传一个值 或多个
     *            删除缓存
     */
    public static void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Lists.newArrayList(key));
            }
        }
    }

    /*************************************** String *********************************************/

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> c) {
        Object o = stringOps.get(key);
        return Objects.nonNull(o) ? (T) o : null;
    }

    public static String get(String key) {
        Object o = stringOps.get(key);
        return Objects.nonNull(o) ? (String) o : null;
    }

    /**
     * @param key    键
     * @param value  值(不支持集合和数组)
     * @param expire 时间(秒) time要大于0 如果expire小于等于0 将设置无限期
     */
    public static void set(String key, Object value, long expire) {
        stringOps.set(key, value, expire, TimeUnit.SECONDS);
    }

    public static void set(String key, Object value, long expire, TimeUnit timeUnit) {
        stringOps.set(key, value, expire, timeUnit);
    }

    /**
     * @param key   键
     * @param value 值(不支持集合和数组)
     */
    public static void set(String key, Object value) {
        stringOps.set(key, value);
    }

    /**
     * @param key    键
     * @param expire 时间(秒) time要大于0 如果expire小于等于0 将设置无限期
     * @return true成功 false失败
     */
    public static boolean tryLock(String key, long expire) {
        final long ex = expire <= 0 ? THREE_MINUTES : expire;
        final byte[] k = key.getBytes();
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) redisConnection ->
                Boolean.FALSE.equals(redisConnection.exists(k)) && Boolean.TRUE.equals(redisConnection.set(k, k, Expiration.seconds(ex), RedisStringCommands.SetOption.ifAbsent()))));
    }

    public static void unlock(String key) {
        delete(key);
    }

    public static void DistributedLock(String key, long expire, LockVoidExecute execute) {
        key = "Lock:" + key;
        if (tryLock(key, expire)) {
            try {
                execute.doAction();
            } catch (Exception e) {
                unlock(key);
                log.error("DistributedLock error: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } else {
            log.info("DistributedLock: 未获取到锁 {}", key);
        }
    }

    public static <T> T DistributedLock(String key, long expire, LockExecute<T> execute) {
        key = "Lock:" + key;
        if (tryLock(key, expire)) {
            try {
                return execute.doAction();
            } catch (Exception e) {
                unlock(key);
                log.error("DistributedLock error: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } else {
            log.info("DistributedLock: 未获取到锁 {}", key);
        }
        return null;
    }

    public interface LockExecute<T> {
        T doAction();
    }

    public interface LockVoidExecute {
        void doAction();
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public static long increment(String key, long delta) {
        if (delta < 0) {
            log.error("class:RedisUtils method:increasing() => delta:{} delta must more than 0", delta);
        }
        Long n = stringOps.increment(key, delta);
        if (n == null) {
            stringOps.set(key, delta);
            return delta;
        }
        return n;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public static long decrement(String key, long delta) {
        if (delta < 0) {
            log.error("class:RedisUtils method:decrement() => delta:{} delta must more than 0", delta);
        }
        Long n = stringOps.decrement(key, delta);
        if (n == null) {
            stringOps.set(key, 0);
            return 0;
        }
        return n;
    }

    /*************************************** Hash *********************************************/

    /**
     * 获取hashKey对应的hashKey值
     */
    @SuppressWarnings("unchecked")
    public static <T> T hget(String key, String hkey, Class<T> c) {
        return (T) hashOps.get(key, hkey);
    }

    /**
     * 获取hashKey对应的所有键值
     */
    public static Map<String, Object> hmget(String key) {
        return hashOps.entries(key);
    }

    /**
     * HashSet map
     */
    public static boolean hmset(String key, Map<String, Object> map) {
        try {
            hashOps.putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:hmset() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * HashSet map 并设置时间
     */
    public static boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            hashOps.putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:hmset() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 向hash表中放入一条数据,如果不存在将创建
     */
    public static boolean hset(String key, String hkey, Object value) {
        try {
            hashOps.put(key, hkey, value);
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:hset() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 向hash表中放入一条数据,如果不存在将创建
     * 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     */
    public static boolean hset(String key, String hkey, Object value, long time) {
        try {
            hashOps.put(key, hkey, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:hset() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 删除hash表中的hkeys值
     */
    public static void hdel(String key, Object... hkeys) {
        hashOps.delete(key, hkeys);
    }

    /**
     * 判断hash表中是否有该项的值
     */
    public static boolean hHasKey(String key, Object hkey) {
        return hashOps.hasKey(key, hkey);
    }

    /*************************************** List *********************************************/

    public static <T> List<T> lrange(String key, long start, long end, Class<T> c) {
        return (List<T>) listOps.range(key, start, end);
    }

    public static <T> List<T> list(String key, Class<T> c) {
        return (List<T>) listOps.range(key, 0, -1);
    }

    /**
     * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头
     */
    public static Long lLeftPush(String key, Object... value) {
        if (value.length > 1) {
            return listOps.leftPushAll(key, value);
        }
        return listOps.leftPush(key, value);
    }

    /**
     * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
     */
    public static Long lRightPush(String key, Object... value) {
        if (value.length > 1) {
            return listOps.rightPushAll(key, value);
        }
        return listOps.rightPush(key, value);
    }

    /**
     * 实现命令：LPOP key，移除并返回列表 key的头元素。
     */
    @SuppressWarnings("unchecked")
    public static <T> T lLeftPop(String key, Class<T> c) {
        return (T) parseJSONArray(listOps.rightPop(key));
    }

    /**
     * 实现命令：LPOP key，移除并返回列表 key的头元素。
     */
    @SuppressWarnings("unchecked")
    public static <T> T lRightPop(String key, Class<T> c) {
        return (T) parseJSONArray(listOps.leftPop(key));
    }

    private static Object parseJSONArray(Object o) {
        if (ObjectUtil.isEmpty(o)) {
            return null;
        }
        if (o instanceof JSONArray) {
            return ((JSONArray) o).get(0);
        }
        return o;
    }

    /*************************************** Set *********************************************/

    public static Long setAdd(String key, Object values) {
        return setOps.add(key, values);
    }

    public static Long setAdd(String key, long seconds, Object... values) {
        if (ObjectUtil.isEmpty(values)) {
            return 0L;
        }
        Long l = setOps.add(key, values);
        if (seconds > 0) {
            expire(key, seconds);
        }
        return l;
    }

    public static Long setRemove(String key, Object... values) {
        return setOps.remove(key, values);
    }

    /**
     * 删除并返回count个元素
     */
    public static <T> List<T> setPop(String key, long count, Class<T> c) {
        return (List<T>) setOps.pop(key, count);
    }

    /**
     * 删除并返回一个元素
     */
    public static Object setPop(String key) {
        return setOps.pop(key);
    }

    public static Long setSize(String key) {
        return setOps.size(key);
    }

    /**
     * 判断集合是否包含value
     */
    public static Boolean isSetMember(String key, Object value) {
        return setOps.isMember(key, value);
    }

    public static <T> Set<T> setMembers(String key, Class<T> c) {
        return (Set<T>) setOps.members(key);
    }

    public static <T> T randomSetMember(String key, Class<T> c) {
        return (T) setOps.randomMember(key);
    }


    /**
     * 获取两个集合的交集
     */
    public static Set<Object> intersect(String key, String key2) {
        return setOps.intersect(key, key2);
    }

    /**
     * 获取多个集合的交集
     */
    public static Set<Object> intersect(Collection<String> keys) {
        return setOps.intersect(keys);
    }

    /**
     * 获取两个集合的差集
     */
    public static Set<Object> difference(String key, String key2) {
        return setOps.difference(key, key2);
    }

    /**
     * 获取多个集合的差集
     */
    public static Set<Object> difference(Collection<String> keys) {
        return setOps.difference(keys);
    }


    public static <T> Set<T> distinctRandomMembers(String key, int rows, Class<T> c) {
        return (Set<T>) setOps.distinctRandomMembers(key, rows);
    }

    /*************************************** ZSet *********************************************/

    public static Boolean zadd(String key, Object value, double sort) {
        return zsetOps.add(key, value, sort);
    }

    public static <T> Set<T> zrange(String key, long start, long end, Class<T> c) {
        return (Set<T>) zsetOps.range(key, start, end);
    }

    public static <T> Set<T> zRangeReverse(String key, long start, long end, Class<T> c) {
        return (Set<T>) zsetOps.reverseRange(key, start, end);
    }

    public static Long zRemove(String key, Object... values) {
        return zsetOps.remove(key, values);
    }

    public static Long zSize(String key) {
        return zsetOps.size(key);
    }

}
