package nio;

import common.util.ExceptionUtil;
import common.util.FindClassUtils;
import common.util.SerializableUtil;
import common.util.ThreadUtil;
import nio.core.User;
import nio.message.Head;
import nio.message.RemoteServicePackage;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

class ServiceHandler {
    private static final Map<Class<?>, RpcService> ALL_SERVICE = new ConcurrentHashMap<>();

    private static final ArrayList<LinkedBlockingQueue<Node>> SERVICE_QUEUES = new ArrayList<>();
    private static volatile int index = 0;

    static {
        FindClassUtils.getClasses(true)
                .stream()
                .filter(clazz -> clazz.getSuperclass() == RpcService.class)
                .forEach(clazz -> {
                    try {
                        RpcService service = (RpcService) clazz.newInstance();
                        ALL_SERVICE.put(clazz, service);
                    } catch (InstantiationException | IllegalAccessException e) {
                        ExceptionUtil.throwT(e);
                    }
                });
        for (int i = 0; i < 4; i++) {
            LinkedBlockingQueue<Node> queue = new LinkedBlockingQueue<>();
            ThreadUtil.createLoopThread(() -> {
                try {
                    Node take = queue.take();
                    byte[] poll = take.bytes;
                    int headl = RpcService.getLength(poll);
                    Head head = SerializableUtil.deSerializable(poll, 4, headl);
                    byte[] data = Arrays.copyOfRange(poll, headl + 4, poll.length);
                    RemoteServicePackage msg = new RemoteServicePackage(head, data);
                    doNode(msg, take.channel);
                } catch (InterruptedException e) {
                    ExceptionUtil.throwT(e);
                } catch (Exception e) {
                    ExceptionUtil.print(e);
                }
            }, "service-work-" + i)
                    .start();
            SERVICE_QUEUES.add(queue);
        }
    }


    private static void doNode(RemoteServicePackage msg, SocketChannel channel) {
        if (User.SELF.windows) {
            doService(msg, channel);
        } else if (msg.head.getter == null) {
            doService(msg, channel);
            ChannelHandler.send(msg);
        } else {
            User user = msg.head.getter.getUser();
            if (user == User.SELF) {
                doService(msg, channel);
            } else {
                ChannelHandler.send(msg);
            }
        }
    }

    private static void doService(RemoteServicePackage msg, SocketChannel channel) {
        RpcService rpcService = ALL_SERVICE.get(msg.head.rpcService);
        rpcService.service(msg.head.obj, channel, msg.data);
    }


    static Consumer<byte[]> getMessageReorganise(SocketChannel channel) {
        int i = index;
        index = (i + 1) % SERVICE_QUEUES.size();
        LinkedBlockingQueue<Node> nodes = SERVICE_QUEUES.get(i);
        return b -> nodes.add(new Node(channel, b));
    }

    private static class Node {
        SocketChannel channel;
        byte[] bytes;

        private Node(SocketChannel channel, byte[] bytes) {
            this.channel = channel;
            this.bytes = bytes;
        }
    }
}
