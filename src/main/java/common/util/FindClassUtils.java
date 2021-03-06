package common.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author qr
 * 这个类太基础了，是读取其他类的类，里面的控制台输出有必要保留，没法采用日志的形式，因为这个类运行期间，日志类tm的还没加载呢
 */
public class FindClassUtils {
    private static final String JAR_KIND = ".jar";
    private static final String CLASS_KIND = ".class";
    private static final int CLASS_KIND_LENGTH = CLASS_KIND.length();
    private static Class<?>[] SYSTEM_CLASSES;
    private static Set<Class<?>> CLASSES;

    public static synchronized Class<?>[] getSystemClasses() {
        if (SYSTEM_CLASSES == null) {
            final long begin = System.currentTimeMillis();
            System.out.println("try find all system class");
            Set<Class<?>> systemClasses = new ConcurrentSkipListSet<>(Comparator.comparing(Class::getName));
            try {
                final String property = System.getProperty("sun.boot.class.path");
                System.out.println("SystemProperty:\"sun.boot.class.path\":" + property);
                final String[] systemClassPaths = property.split(";");
                for (String systemClassPath : systemClassPaths) {
                    fileClassLoad(new File(systemClassPath), systemClassPath.length(), systemClasses);
                }
                SYSTEM_CLASSES = systemClasses.toArray(new Class<?>[systemClasses.size()]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.err.println("system class success found in " + (begin - System.currentTimeMillis() + " ms"));
        }
        return Arrays.copyOf(SYSTEM_CLASSES, SYSTEM_CLASSES.length);
    }

    /**
     * 从包package中获取所有的Class
     */
    public static synchronized Set<Class<?>> getClasses(boolean cache) {
        if (CLASSES != null && cache) {
            return CLASSES;
        }
        final long begin = System.currentTimeMillis();
        System.err.println("try find all user class");
        // 第一个class类的集合
        Set<Class<?>> classes = new ConcurrentSkipListSet<>(Comparator.comparing(Class::getName));
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        String path;

        final URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource("");
        try {
            assert resource != null;
            System.err.println("is dev classes");
            path = Paths.get(resource.toURI())
                    .toString();
        } catch (NullPointerException | URISyntaxException e) {
            System.err.println("not dev , try project");
            path = Paths.get(System.getProperty("sun.java.command"))
                    .toString();
        }
        fileClassLoad(new File(path), path.length(), classes);
        CLASSES = classes;
        System.err.println("user class success found in " + (begin - System.currentTimeMillis() + " ms"));
        return CLASSES;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     */
    private static void fileClassLoad(File scannerFile, int rootPathLength, Set<Class<?>> classes) {
        // 如果不存在或者 也不是目录就直接返回
        if (scannerFile == null || !scannerFile.exists()) {
            System.err.println("not exists : " + scannerFile);
            return;
        }
        // 如果是目录 则继续扫描
        if (scannerFile.isDirectory()) {
            File[] files = scannerFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    fileClassLoad(file, rootPathLength, classes);
                }
            }
            return;
        }
        final String path = scannerFile.getAbsolutePath();
        if (path.endsWith(CLASS_KIND)) {
            final String className = path.substring(rootPathLength, path.length() - CLASS_KIND_LENGTH)
                    .replaceAll("[\\\\/]", ".");
            fileClassLoad(className.startsWith(".") ? className.substring(1) : className, classes);
            return;
        }
        if (path.endsWith(JAR_KIND)) {
            // 获取jar
            try {
                jarClassLoad(new JarFile(scannerFile), classes);
            } catch (IOException e) {
                System.err.println("load jar fail : " + path);
            }
        }
    }

    private static void jarClassLoad(JarFile jar, Set<Class<?>> classes) {
        // 从此jar包 得到一个枚举类
        Enumeration<JarEntry> entries = jar.entries();
        // 同样的进行循环迭代
        while (entries.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(CLASS_KIND)) {
                fileClassLoad(name.substring(0, name.length() - CLASS_KIND_LENGTH)
                        .replaceAll("[\\\\/]", "."), classes);
            }
        }
    }


    private static void fileClassLoad(String className, Set<Class<?>> classes) {
        try {
            classes.add(Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass(className));
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            System.err.println("load fail : " + className);
        }
    }
}
