package nio;

import common.util.ExceptionUtil;
import common.util.ProxyUtil;
import common.util.StaticBeanFactory;
import common.util.ThreadUtil;
import dto.UserDto;
import nio.core.User;
import nio.message.RemoteServicePackage;
import service.GetUsersInfo;
import service.UpdateUser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author 88382571
 * 2019/5/16
 */
public class ChannelHandler {

    static {
        Runnable[] first = new Runnable[1];
        first[0] = () -> {
            Optional<User> any = User.getAll()
                    .filter(a -> !a.windows)
                    .filter(a -> isConnected(a.getChannel()))
                    .findAny();
            if (any.isPresent()) {
                User linux = any.get();
                //通知其他用户登录
                ChannelHandler.send(new RemoteServicePackage<>(null, UpdateUser.class, new UserDto(User.SELF)));
                //获取其他用户信息
                ChannelHandler.send(new RemoteServicePackage<>(linux, GetUsersInfo.class, new UserDto(User.SELF)));
                first[0] = () -> {
                };
            }
        };
        //心跳连接所有linux
        ThreadUtil.createLoopThread(() -> {
//            User.getAll().filter(a -> a != User.SELF).filter(user -> user.getStatus() != -1).filter(a -> !a.windows).forEach(ChannelHandler::handlerNew);
            first[0].run();
            try {
                Thread.sleep(15000L);
            } catch (InterruptedException e) {
                ExceptionUtil.throwT(e);
            }
        }, "linux linker")
                .start();
    }

    static void init() {
    }

    private static final Map<User, List<RemoteServicePackage>> SEND_FAIL = new ConcurrentHashMap<>();
    private static final ProxyUtil.FieldProxy<Object> WRITE_LOCK = ProxyUtil.getField(ProxyUtil.getClass("sun.nio.ch.SocketChannelImpl"), "writeLock");

    public static void send(SocketChannel channel, RemoteServicePackage buffers) {
        try {
            synchronized (WRITE_LOCK.get(channel)) {
                ByteBuffer[] srcs = buffers.byteBuffers();
                long l = 0;
                for (ByteBuffer src : srcs) {
                    l += src.limit();
                }
                long write = 0;
                while (l != write) {
                    write += channel.write(srcs);
                }
            }
        } catch (Exception e) {
            ExceptionUtil.print(e);
        }
    }

    private static boolean isConnected(SocketChannel channel) {
        return channel != null && channel.isConnected();
    }

    public static void send(RemoteServicePackage msg) {
        //本地只发送给任一服务器
        if (User.SELF.windows) {
            Optional<SocketChannel> any = User.getAll()
                    .filter(a -> !a.windows)
                    .map(User::getChannel)
                    .filter(ChannelHandler::isConnected)
                    .findAny();
            if (any.isPresent()) {
                send(any.get(), msg);
            } else {
                User.SELF.newMsg("操作执行失败！\n远程服务器全部离线！\n请联系 18715600499 钱睿 修复");
            }
            return;
        }
        //自身是linux
        UserDto getter = msg.head.getter;
        //群发给其他节点，报文修改为指定接收方
        if (getter == null) {
            User.getAll()
                    .filter(u -> u != User.SELF)
                    .filter(u -> isConnected(u.getChannel()))
                    .forEach(u -> send(u.getChannel(), msg.newGetter(u)));
            return;
        }

        User user = getter.getUser();
        SocketChannel channel = user.getChannel();
        if (isConnected(channel)) {
            send(channel, msg);
        } else if (user.windows) {
            SEND_FAIL.computeIfAbsent(user, u -> Collections.synchronizedList(new ArrayList<>()))
                    .add(msg);
        }
    }

    private static void handlerNew(User user) {
        SocketChannel channel = user.getChannel();
        //老通道可用
        if (!isConnected(channel)) {
            //老的不可用
            channel = newChannelClient(user);
        }
        //逻辑加入
        if (channel != null) {
            send(channel, new RemoteServicePackage<>(user, JoinService.class, new UserDto(User.SELF)));
        }
    }

    static void removeChannel(Exception e, SocketChannel channel) {
        if (e != null && !"Connection reset by peer".equals(e.getMessage()) && !(e instanceof java.nio.channels.ClosedChannelException)) {
            ExceptionUtil.print(e);
        }
        try {
            channel.close();
        } catch (IOException never) {
            //never
        }
        User.getAll()
                .filter(a -> a.getChannel() == channel)
                .forEach(a -> a.removeChannel(channel));
    }

    private static final int WAIT_CONNECT_TIMES = 5;

    private static SocketChannel newChannelClient(User user) {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(user.host, user.mainPort));
            for (int i = 0; i < WAIT_CONNECT_TIMES; i++) {
                if (channel.isConnectionPending()) {
                    if (channel.finishConnect()) {
                        user.setChannel(channel);
                        StaticBeanFactory.get(ServerBoot.class)
                                .register(channel);
                        return channel;
                    }
                }
                Thread.sleep(100L);
            }
            channel.close();
        } catch (SocketException | ClosedByInterruptException e) {
            //hidden
        } catch (Exception e) {
            ExceptionUtil.print(e);
        }
        System.out.println("connect fail " + user);
        return null;
    }

    public static class JoinService extends RpcService<UserDto> {
        @Override
        public void service(UserDto obj, SocketChannel channel, byte[] data) {
            User sender = obj.getUser();
            sender.setChannel(channel)
                    .setName(obj.name);
            List<RemoteServicePackage> packages = SEND_FAIL.remove(sender);
            if (packages != null) {
                packages.forEach(p -> send(channel, p));
            }
        }
    }
}
