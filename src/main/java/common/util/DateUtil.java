package common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 88382571
 * 2018/5/31
 */
public class DateUtil {

    private static final Map<String, ThreadLocal<SimpleDateFormat>> SIMPLE_DATE_FORMAT_FACTORY = new ConcurrentHashMap<>();

    private static ThreadLocal<SimpleDateFormat> getSimpleDateFormat(String format) {
        return SIMPLE_DATE_FORMAT_FACTORY.computeIfAbsent(format,
                a -> ThreadLocal.withInitial(() -> new SimpleDateFormat(a)));
    }

    public static String format(String format, Date date) {
        return date == null ? null : getSimpleDateFormat(format).get()
                .format(date);
    }

    public static Date parse(String format, String str) {
        try {
            return str == null ? null : getSimpleDateFormat(format).get()
                    .parse(str);
        } catch (ParseException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static String format(String format, long date) {
        return format(format, new Date(date));
    }

}
