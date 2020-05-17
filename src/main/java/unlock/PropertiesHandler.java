package unlock;

import common.CmdInstructions;
import common.util.ExceptionUtil;
import common.util.StaticBeanFactory;
import common.util.ThreadUtil;
import view.ViewRoot;

import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author 88382571
 * 2019/4/22
 */
public class PropertiesHandler {
    private static final TimeNode[][] USE_NODES = new TimeNode[24][60];
    private static final File PROPERTIES = new File(System.getProperty("user.dir") + "/never.properties");
    private static volatile Properties PRO;
    public static final Map<String, Consumer<String>> LISTEN_CHANGE_PRO = new ConcurrentHashMap<>();
    private static volatile boolean IsLock = false;
    private static volatile boolean IsNever = false;

    public static void init() {
        LISTEN_CHANGE_PRO.put("IsLock", v -> IsLock = Boolean.parseBoolean(v));
        LISTEN_CHANGE_PRO.put("IsNever", v -> IsNever = Boolean.parseBoolean(v));
        if (!PROPERTIES.exists()) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(PROPERTIES), "UTF-8"); Reader reader = new InputStreamReader(PropertiesHandler.class.getClassLoader()
                    .getResourceAsStream("never.properties"), "UTF-8")) {
                Properties properties = new Properties();
                properties.load(reader);
                properties.store(writer, "default");
            } catch (IOException e) {
                //never
                ExceptionUtil.throwT(e);
            }
        }

        userProInit();
        String name = PROPERTIES.getName();
        Path path = Paths.get(PROPERTIES.getParentFile()
                .getPath());
        Thread ret = ThreadUtil.createThread(() -> {
            try (WatchService watchService = FileSystems.getDefault()
                    .newWatchService()) {
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
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
                ExceptionUtil.print(e);
            }
        }, "properties-listener");
        ret.setDaemon(true);
        ret.start();
        ViewRoot viewRoot = StaticBeanFactory.get(ViewRoot.class);
        ThreadUtil.createLoopThread(() -> {
            try {
                NeverLock.unlock(IsNever);
                LocalTime now = LocalTime.now();
                tryNode(now.getHour(), now.getMinute(), viewRoot);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, "never-lock")
                .start();
    }

    private static void tryNode(int hour, int minute, ViewRoot viewRoot) throws InterruptedException, IOException {
        PropertiesHandler.TimeNode timeNode = PropertiesHandler.USE_NODES[hour][minute];
        if (timeNode != null) {
            boolean isFirst = timeNode != lastTimeNode;
            if (timeNode.lock) {
                if (IsLock) {
                    if (timeNode.loop || isFirst) {
                        CmdInstructions.lock();
                    }
                }
            } else if (isFirst) {
                System.out.println(timeNode);
                viewRoot.show(timeNode.getMsg(), timeNode.time);
            }
        }
        lastTimeNode = timeNode;
    }

    private static volatile PropertiesHandler.TimeNode lastTimeNode;

    private static void userProInit() {
        if (PROPERTIES.exists() && PROPERTIES.isFile()) {
            for (TimeNode[] useNode : USE_NODES) {
                for (int i = 0; i < useNode.length; i++) {
                    useNode[i] = null;
                }
            }
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(PROPERTIES), "UTF-8")) {
                Properties properties = new Properties();
                properties.load(reader);
                putProperties(properties);
                for (Map.Entry<String, Consumer<String>> entry : LISTEN_CHANGE_PRO.entrySet()) {
                    String key = entry.getKey();
                    String nValue = properties.getProperty(key);
                    String oValue = PRO != null ? PRO.getProperty(key) : null;
                    if (nValue != null && !nValue.equals(oValue)) {
                        try {
                            entry.getValue()
                                    .accept(nValue);
                        } catch (Exception e) {
                            ExceptionUtil.print(e);
                        }
                    }
                }
                PRO = properties;
            } catch (IOException e) {
                ExceptionUtil.print(e);
            }
        }
    }

    private static void putProperties(Properties properties) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            try {
                putNode((String) entry.getKey(), (String) entry.getValue());
            } catch (Exception e) {
                ExceptionUtil.print(e);
            }
        }
    }

    private static void putNode(String key, String value) {
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
            USE_NODES[hour][minute] = null;
            return;
        }
        split = value.split("\\|");
        if ("lock".equalsIgnoreCase(split[0].trim())) {
            boolean loop = split.length > 1 && "loop".equalsIgnoreCase(split[1].trim());
            USE_NODES[hour][minute] = new TimeNode(hour, minute, true, loop, null, 0);
        } else {
            String msg = split[0];
            int time = split.length > 1 ? Integer.parseInt(split[1].trim()) : 3;
            USE_NODES[hour][minute] = new TimeNode(hour, minute, false, false, msg, time);
        }
    }

    private static class TimeNode {
        private final int hour;
        private final int minute;
        private final boolean lock;
        private final boolean loop;
        private final String msg;
        private final int time;

        TimeNode(int hour, int minute, boolean lock, boolean loop, String msg, int time) {
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

        @Override
        public String toString() {
            return "TimeNode{" + "hour=" + hour + ", minute=" + minute + ", lock=" + lock + ", loop=" + loop + ", msg='" + msg + '\'' + ", time=" + time + '}';
        }
    }
}
