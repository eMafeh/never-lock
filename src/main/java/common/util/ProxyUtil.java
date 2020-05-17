package common.util;

import java.lang.reflect.Field;

/**
 * @author 88382571
 * 2019/6/10
 */
public class ProxyUtil {

    @SuppressWarnings("all")
    public static <T> FieldProxy<T> getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return new FieldProxy(field);
        } catch (NoSuchFieldException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static <T> T getStatic(Class<?> clazz, String name) {
        FieldProxy<T> field = getField(clazz, name);
        return field.get(null);
    }

    public static class FieldProxy<T> {
        private final Field field;

        private FieldProxy(Field field) {
            this.field = field;
        }

        @SuppressWarnings("all")
        public T get(Object obj) {
            try {
                return (T) field.get(obj);
            } catch (IllegalAccessException e) {
                return ExceptionUtil.throwT(e);
            }
        }

        public void set(Object obj, T value) {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                ExceptionUtil.throwT(e);
            }
        }
    }
}
