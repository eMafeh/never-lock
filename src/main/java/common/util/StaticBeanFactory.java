package common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StaticBeanFactory {
    private static final Map<Class<?>, Object> CACHE = new ConcurrentHashMap<>();

    public static <T> void put(Class<T> tClass, T t) {
        CACHE.put(tClass, t);
    }

    @SuppressWarnings("all")
    public static <T> T get(Class<T> tClass) {
        return (T) CACHE.get(tClass);
    }
}
