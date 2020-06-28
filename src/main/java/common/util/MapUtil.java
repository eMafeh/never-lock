package common.util;

import java.util.List;
import java.util.Map;

public class MapUtil {

    @SuppressWarnings("all")
    public static <R> R get(final Object map, Object... keys) {
        Object result = map;
        for (Object key : keys) {
            if (key instanceof String) result = ((Map) result).get(key);
            else if (key instanceof Integer) result = ((List) result).get((Integer) key);
            else throw new RuntimeException(key.getClass() + " not support");
        }
        return (R) result;
    }
}
