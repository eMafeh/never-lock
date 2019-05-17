package source;

import common.util.ThreadUtil;
import lombok.ToString;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesHandler {
    public static final PropertiesHandler.TimeNode[][] NODES = new TimeNode[24][];
    public static final PropertiesHandler.TimeNode[][] USE_NODES = new TimeNode[24][];
    public static final File PROPERTIES = new File(System.getProperty("user.dir") + "/never.properties");

    public static void init() {
        for (int i = 0; i < NODES.length; i++) {
            NODES[i] = new TimeNode[60];
            USE_NODES[i] = new TimeNode[60];
        }

        try (Reader reader = new InputStreamReader(PropertiesHandler.class.getClassLoader()
                .getResourceAsStream("never.properties"), "UTF-8")) {
            Properties properties = new Properties();
            properties.load(reader);
            putProperties(properties, NODES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userProInit();
        File parentFile = PROPERTIES.getParentFile();
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            throw new RuntimeException("parent is not directory " + parentFile);
        }
        String name = PROPERTIES.getName();
        Path path = Paths.get(parentFile.getPath());
        Thread ret = ThreadUtil.createThread(() -> {
            try (WatchService watchService = FileSystems.getDefault()
                    .newWatchService()) {
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    WatchKey take = watchService.take();
                    List<WatchEvent<?>> watchEvents = take.pollEvents();
                    for (WatchEvent<?> watchEvent : watchEvents) {
                        if (watchEvent.context() instanceof Path) {
                            Path context = (Path) watchEvent.context();
                            if (name.equals(context.toString())) {
                                userProInit();
                                break;
                            }
                        }
                    }
                    take.reset();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, "properties");
        Thread thread = ret;
        ret.setDaemon(true);
        ret.start();
    }


    private static void userProInit() {
        for (int i = 0; i < NODES.length; i++) {
            System.arraycopy(NODES[i], 0, USE_NODES[i], 0, NODES[i].length);
        }
        if (PROPERTIES.exists() && PROPERTIES.isFile()) {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(PROPERTIES), "UTF-8")) {
                Properties properties = new Properties();
                properties.load(reader);
                putProperties(properties, USE_NODES);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void putProperties(final Properties properties, final TimeNode[][] nodes) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            try {
                putNode((String) entry.getKey(), (String) entry.getValue(), nodes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void putNode(final String key, final String value, final TimeNode[][] nodes) {

        String[] split = key.replace((char) 65279, '0')
                .split("-");
        if (split.length < 2) {
            return;
        }
        int hour = Integer.parseInt(split[0].trim());
        int minute = Integer.parseInt(split[1].trim());
        if (hour < 0 || hour > 24 || minute < 0 || minute > 60) {
            throw new RuntimeException("hour-minute " + key);
        }
        if (value.isEmpty()) {
            nodes[hour][minute] = null;
            return;
        }
        split = value.split("\\|");
        if ("lock".equalsIgnoreCase(split[0].trim())) {
            boolean loop = split.length > 1 && "loop".equalsIgnoreCase(split[1].trim());
            nodes[hour][minute] = new TimeNode(hour, minute, true, loop, null, 0);
        } else {
            String msg = split[0];
            int time = split.length > 1 ? Integer.parseInt(split[1].trim()) : 3;
            nodes[hour][minute] = new TimeNode(hour, minute, false, false, msg, time);
        }
    }

    @ToString
    public static class TimeNode {
        private final int hour;
        private final int minute;
        private final String msg;
        public boolean lock;
        public boolean loop;
        public int time;

        public TimeNode(final int hour, final int minute, final boolean lock, final boolean loop, final String msg, final int time) {
            this.hour = hour;
            this.minute = minute;
            this.lock = lock;
            this.loop = loop;
            this.msg = msg;
            this.time = time;
        }

        public String getMsg() {
            return hour + "点" + (minute == 0 ? "整" : minute + "分") + " : " + msg;
        }

    }
}
