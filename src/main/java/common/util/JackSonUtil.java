package common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class JackSonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> map = new TypeReference<Map<String, Object>>() {
    };

    public static Map<String, Object> parse(String s) {
        try {
            return objectMapper.readValue(s, map);
        } catch (IOException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static Map<String, Object> parse(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, map);
        } catch (IOException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static Map<String, Object> parsePath(Path path) {
        try {
            return objectMapper.readValue(Files.readAllBytes(path), map);
        } catch (IOException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static String toString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static String toPrettyPrinterString(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(obj);
        } catch (IOException e) {
            return ExceptionUtil.throwT(e);
        }
    }
}
