package common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class LogUtil {
    public static void changeSystemOut(String fileName) throws IOException {
        File file = new File(fileName);
        ExceptionUtil.isTrue(!file.isDirectory(), () -> fileName + " is a directory");
        ExceptionUtil.isTrue(file.exists() || file.createNewFile(), () -> fileName + " create fail");
        ExceptionUtil.isTrue(file.canWrite(), () -> fileName + " can not write");
        FileOutputStream outputStream = new FileOutputStream(file, true);
        PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8.displayName());
        System.setOut(printStream);
    }
}
