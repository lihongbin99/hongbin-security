package io.lihongbin.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadPool {

    public final static String SOCKET_GROUP = "SOCKET_THREAD_GROUP";

    private final static Map<String, Map<String, Thread>> POOL = new ConcurrentHashMap<>();

    public static void put(String group, String name, Thread thread) {
        Map<String, Thread> threadMap = POOL.get(group);
        if (null == threadMap) {
            POOL.putIfAbsent(group, new ConcurrentHashMap<>());
            threadMap = POOL.get(group);
        }
        threadMap.put(name, thread);
    }

    public static void remove(String group, String name) {
        POOL.get(group).remove(name);
    }

    public static Map<String, Thread> get(String group) {
        return POOL.get(group);
    }

    public static int count(String group) {
        Map<String, Thread> threadMap = POOL.get(group);
        return null == threadMap ? 0 : threadMap.size();
    }

}
