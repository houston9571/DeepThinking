package com.deepthinking.common.thread;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 2016年10月31日 上午11:11:57
 * Version 1.0
 */
public class ThreadLocalManager {

    private static ConcurrentHashMap manager = new ConcurrentHashMap();

    private static String key = "task_stoped";

    public static <T> T get(Object key, Class<T> clazz, Object defaults) {
        Object object = manager.get(key);
        if (object == null)
            return (T) defaults;
        return (T) manager.get(key);
    }

    public static void set(Object key, Object value) {
        manager.put(key, value);
    }

    public static void remove(Object key) {
        manager.remove(key);
    }

    public static void startTask() {
        set(key, false);
    }

    public static void finishTask() {
        set(key, true);
    }

    public static boolean isTaskFinish() {
        return get(key, boolean.class, false);
    }

    public static void initTaskFlag() {
        set(key, true);
    }
}
