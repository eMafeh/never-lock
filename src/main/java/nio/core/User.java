package nio.core;

import common.util.ThreadUtil;
import nio.RpcService;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class User {
    public final String host;
    public final int mainPort;
    private String name;
    public final List<Consumer<String>> listenName = Collections.synchronizedList(new ArrayList<>());
    /**
     * 1 在线 0 离线
     */
    private int status;
    public final List<IntConsumer> listenStatus = Collections.synchronizedList(new ArrayList<>());
    public final List<Consumer<String>> listenGetMsg = Collections.synchronizedList(new ArrayList<>());
    public final List<SendMessageListener> listenSendMsg = Collections.synchronizedList(new ArrayList<>());
    private static final ThreadPoolExecutor EXECUTOR = ThreadUtil.createPool(1, 1, "user-change");
    public static final List<Consumer<User>> LISTEN_CREATE = Collections.synchronizedList(new ArrayList<>());
    /**
     * 确保 ip port 一定 全局一定只有一个user
     */
    private static final Map<String, User> SINGLE_POOL = new ConcurrentHashMap<>();

    private User(final String host, final int mainPort) {
        this.host = host;
        this.mainPort = mainPort;
    }

    public static User getUser(String host, int mainPort) {
        return SINGLE_POOL.computeIfAbsent(uniqueIdentifier(host, mainPort), a -> {
            User user = new User(host, mainPort);
            for (Consumer<User> userConsumer : LISTEN_CREATE) {
                userConsumer.accept(user);
            }
            return user;
        });
    }

    public void newMsg(String msg) {
        for (Consumer<String> a : listenGetMsg) {
            EXECUTOR.execute(() -> a.accept(msg));
        }
    }

    public <T extends Serializable> void sendMsg(Class<? extends RpcService<? super T>> rpcService, T obj, int again) {
        for (SendMessageListener a : listenSendMsg) {
            EXECUTOR.execute(() -> a.call(rpcService, obj, again));
        }
    }

    public User setName(final String name) {
        if (name == null) return this;
        final String oldName = this.name;
        this.name = name;
        if (!name.equals(oldName)) {
            for (Consumer<String> c : listenName) {
                EXECUTOR.execute(() -> c.accept(name));
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

    public int getStatus() {
        return status;
    }

    public User setStatus(final int status) {
        int oldStatus = this.status;
        this.status = status;
        if (status != oldStatus) {
            for (IntConsumer c : listenStatus) {
                EXECUTOR.execute(() -> c.accept(status));
            }
        }
        return this;
    }

    public String uniqueIdentifier() {
        return uniqueIdentifier(host, mainPort);
    }

    public static String uniqueIdentifier(String host, int mainPort) {
        return host + ":" + mainPort;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final User user = (User) o;
        return mainPort == user.mainPort &&
                host.equals(user.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, mainPort);
    }

    @Override
    public String toString() {
        return "User{" +
                "host='" + host + '\'' +
                ", mainPort=" + mainPort +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
