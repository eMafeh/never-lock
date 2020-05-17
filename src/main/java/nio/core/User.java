package nio.core;

import common.util.SystemUtil;
import common.util.ThreadUtil;

import java.io.File;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

/**
 * @author 88382571
 * 2019/5/7
 */
public class User implements Comparable<User> {
    /**
     * 序列化user
     */
    public final String host;
    public final int mainPort;
    public final boolean windows;

    private String name;
    public final List<Consumer<String>> listenName = Collections.synchronizedList(new ArrayList<>());
    /**
     * 1  在线 0 离线 -1 移除
     */
    private int status;
    public final List<IntConsumer> listenStatus = Collections.synchronizedList(new ArrayList<>());

    public final List<Consumer<String>> listenGetMsg = Collections.synchronizedList(new ArrayList<>());
    public final List<Consumer<String>> listenSendMsg = Collections.synchronizedList(new ArrayList<>());

    public final List<Consumer<File>> listenGetFile = Collections.synchronizedList(new ArrayList<>());
    public final List<Consumer<File>> listenSendFile = Collections.synchronizedList(new ArrayList<>());
    private SocketChannel channel;

    public SocketChannel getChannel() {
        return channel;
    }

    public synchronized User setChannel(SocketChannel channel) {
        if (channel != null && channel.isConnected() && this.channel != channel) {
            this.channel = channel;
            setStatus(1);
            System.out.println("connect success " + this);
        }
        return this;
    }

    public synchronized User removeChannel(SocketChannel channel) {
        if (this.channel == channel) {
            this.channel = null;
            setStatus(0);
            System.err.println("close " + this);
        }
        return this;
    }

    private static final ThreadPoolExecutor EXECUTOR = ThreadUtil.createPool(1, 1, "user-change");
    private static final List<Consumer<User>> CREATE_LISTENERS = Collections.synchronizedList(new ArrayList<>());
    /**
     * 确保 ip port 一定 全局一定只有一个user
     */
    private static final Map<String, User> SINGLE_POOL = new ConcurrentHashMap<>();
    /**
     * 创建自身实体
     */
    public static final User SELF = User.getUser(SystemUtil.getIP(), Integer.parseInt(System.getProperty("server.main.port")), SystemUtil.isWindows())
            .setStatus(1);

    public static Stream<User> getAll() {
        return SINGLE_POOL.values()
                .stream();
    }

    public static User getUser(String host, int mainPort, boolean windows) {
        return SINGLE_POOL.computeIfAbsent(uniqueIdentifier(host, mainPort), a -> {
            User user = new User(host, mainPort, windows);
            for (Consumer<User> c : CREATE_LISTENERS) {
                c.accept(user);
            }
            return user;
        });
    }

    private User(String host, int mainPort, boolean windows) {
        this.host = host;
        this.mainPort = mainPort;
        this.windows = windows;
    }

    public void newMsg(String msg) {
        listenGetMsg.stream()
                .<Runnable>map(a -> () -> a.accept(msg))
                .forEach(EXECUTOR::execute);
    }

    public void sendMsg(String msg) {
        listenSendMsg.stream()
                .<Runnable>map(a -> () -> a.accept(msg))
                .forEach(EXECUTOR::execute);
    }

    public void newFile(File file) {
        listenGetFile.stream()
                .<Runnable>map(a -> () -> a.accept(file))
                .forEach(EXECUTOR::execute);
    }

    public void sendFile(File file) {
        listenSendFile.stream()
                .<Runnable>map(a -> () -> a.accept(file))
                .forEach(EXECUTOR::execute);
    }

    public User setName(final String name) {
        if (name == null) {
            return this;
        }
        final String oldName = this.name;
        this.name = name;
        if (!Objects.equals(oldName, name)) {
            for (Consumer<String> a : listenName) {
                EXECUTOR.execute(() -> a.accept(name));
            }
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public String getNotNullName() {
        return name == null ? uniqueIdentifier() : name;
    }

    public User setStatus(final int status) {
        final int oldStatus = this.status;
        this.status = status;
        if (!Objects.equals(oldStatus, status)) {
            for (IntConsumer a : listenStatus) {
                EXECUTOR.execute(() -> a.accept(status));
            }
        }
        return this;
    }

    public int getStatus() {
        return status;
    }

    /**
     * 根据 host 和 port 拼装唯一标识
     *
     * @return 地址唯一标识字符串
     */
    public String uniqueIdentifier() {
        return uniqueIdentifier(host, mainPort);
    }

    private static String uniqueIdentifier(String host, int mainPort) {
        return host + ":" + mainPort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return mainPort == user.mainPort && host.equals(user.host);
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + mainPort;
        return result;
    }

    @Override
    public String toString() {
        return "User{" + "host='" + host + '\'' + ", mainPort=" + mainPort + ", name='" + name + '\'' + ", status=" + status + '}';
    }

    @Override
    public int compareTo(User o) {
        int i = o.host.compareTo(this.host);
        return i == 0 ? Integer.compare(o.mainPort, this.mainPort) : i;
    }

    public static void init(Consumer<User> init, boolean onlyNew) {
        if (!onlyNew) {
            getAll().forEach(init);
        }
        CREATE_LISTENERS.add(init);
    }
}
