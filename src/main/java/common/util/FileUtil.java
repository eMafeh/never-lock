package common.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileUtil {
    public static List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static List<String> readAllLines(Path path, Charset cs) {
        try {
            return Files.readAllLines(path, cs);
        } catch (IOException e) {
            return ExceptionUtil.throwT(e);
        }
    }
}
