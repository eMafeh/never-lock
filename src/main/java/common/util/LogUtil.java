package common.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author 88382571
 * 2019/4/25
 */
public class LogUtil {
	public static void changeSystemOut(String fileName) throws IOException {
		File file = new File(fileName);
		if (file.isDirectory()) {
			throw new RuntimeException(fileName + " is a directory");
		}
		if (!file.exists() && !file.createNewFile()) {
			throw new RuntimeException(fileName + " create fail");
		}
		if (!file.canWrite()) {
			throw new RuntimeException(fileName + " can not write");
		}
		FileOutputStream outputStream = new FileOutputStream(file, true);
		PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8.displayName());
		System.setOut(printStream);
	}
}
