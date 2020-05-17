package common.util;

import java.io.IOException;
import java.nio.file.*;

public class JarUtil {
    public static final String JAR_KIND = ".jar";

    public static byte[] read(String root, String jarPath) throws IOException {
        if (root.endsWith(JAR_KIND)) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(root), null)) {
                return Files.readAllBytes(fileSystem.getPath(jarPath));
            }
        } else {
            return Files.readAllBytes(Paths.get(root, jarPath));
        }
    }


    public static void write(String root, String jarPath, byte[] serializable) throws IOException {
        if (root.endsWith(JAR_KIND)) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(Paths.get(root), null)) {
                Files.write(fileSystem.getPath(jarPath), serializable, StandardOpenOption.CREATE);
            }
        } else {
            Files.write(Paths.get(root, jarPath), serializable, StandardOpenOption.CREATE);
        }
    }
}
