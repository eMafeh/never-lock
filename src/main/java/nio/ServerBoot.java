package nio;

import common.util.ThreadUtil;
import dto.UserDto;
import nio.core.SendMessageListener;
import nio.core.User;
import nio.message.GetMessage;
import nio.message.SendMessage;
import service.HelloService;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;
import java.util.Stack;

public class ServerBoot {
    private static final int OPS = SelectionKey.OP_READ;
    private final Selector selector;
    private final Stack<ChannelClient> whileRegisterChannels = new Stack<>();
    private final CacheChannel cacheChannel = new CacheChannel(this);
    private final SendFailAgain sendFailAgain;
    final User self;

    public ServerBoot(User leader, User self) throws IOException {
        this.self = self;
        this.selector = Selector.open();
        int mainPort = self.mainPort;
        cacheChannel.handlerChannel(new ChannelLocal(this));
        System.out.println("open and handle local channel");
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(mainPort), 1000);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("register server port " + mainPort);

        ThreadUtil.createLoopThread(this::run, "selector")
                .start();
        sendFailAgain = new SendFailAgain(this);
        sendListen(leader);
        if (!self.equals(leader)) {
            sendListen(self);
        }
        User.LISTEN_CREATE.add(this::sendListen);
        new JoinService(this);
        leader.sendMsg(HelloService.class, "hello linux", Integer.MAX_VALUE);
    }

    private void run() {
        try {
            register();
            selector.select();
            register();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey selectionKey : selectionKeys) {
                handle(selectionKey);
            }
            selectionKeys.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(final SelectionKey selectionKey) throws IOException {
        SelectableChannel selectableChannel = selectionKey.channel();
        //服务端监听创建新通道
        if (selectableChannel instanceof ServerSocketChannel) {
            SocketChannel channel = ((ServerSocketChannel) selectableChannel).accept();
            ChannelServer server = ChannelServer.handleServer(this, channel);
            channel.configureBlocking(false);
            channel.register(selector, OPS, server);
            System.out.println("register " + server);
        } else if (!(selectableChannel instanceof SocketChannel)) {
            throw new RuntimeException("not support this selectableChannel:" + selectableChannel.getClass());
        }
        if (selectionKey.isReadable()) {
            BaseChannelProxy proxy = (BaseChannelProxy) selectionKey.attachment();
            try {
                giveData(proxy);
            } catch (IOException e) {
                cacheChannel.removeChannel(e, proxy);
            }
        }

    }

    private final ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);

    private void giveData(final BaseChannelProxy proxy) throws IOException {
        SocketChannel channel = proxy.channel;
        buffer.clear();
        while (true) {
            int read = channel.read(buffer);
            if (read == 0 || buffer.position() == buffer.limit()) {
                break;
            } else if (read == -1) {
                channel.close();
            }
        }
        buffer.flip();
        int limit = buffer.limit();
        if (limit != 0) {
            byte[] data = new byte[limit];
            buffer.get(data);
            proxy.reorganise.receive(data);
        }
    }

    void register(ChannelClient channelClient) {
        whileRegisterChannels.push(channelClient);
        selector.wakeup();
    }

    private void register() throws IOException {
        while (!whileRegisterChannels.empty()) {
            ChannelClient pop = whileRegisterChannels.pop();
            pop.channel.configureBlocking(false);
            pop.channel.register(selector, OPS, pop);
            System.out.println("register client channel " + pop);
        }
    }

    private void sendListen(final User user) {
        user.listenSendMsg.add(new SendMessageListener() {
            @Override
            public <T extends Serializable> void call(final Class<? extends RpcService<? super T>> rpcService, final T obj, final int again) {
                send(new SendMessage(user, rpcService, obj, again));
            }
        });
    }

    void send(final SendMessage sendMessage) {
        send(sendMessage, cacheChannel.getChannel(sendMessage.user));
    }


    void send(final SendMessage sendMessage, final BaseChannelProxy channel) {
        try {
            if (channel != null) {
                channel.send(sendMessage);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendFailAgain.tryAgain(sendMessage);
    }


    void service(BaseChannelProxy proxy, GetMessage msg) {
        if (proxy instanceof ChannelServer) {
            if (msg.rpcService instanceof JoinService) {
                ((JoinService) msg.rpcService).join(msg, (ChannelServer) proxy);
            } else {
                System.out.println("can not service noJoin ChannelServer " + msg);
            }
        } else {
            msg.doService();
        }
    }


    static class JoinService extends RpcService<Integer> {
        private final ServerBoot boot;

        JoinService(ServerBoot boot) {
            super(1);
            this.boot = boot;
        }

        @Override
        public void service(final Integer integer, final UserDto sender) {
            throw new RuntimeException("should use method join");
        }

        private void join(GetMessage message, ChannelServer channelServer) {
            int mainPort = (int) message.getObj();
            System.out.println("join " + mainPort + " " + channelServer);
            ChannelServer.ChannelJoin join = channelServer.toJoin(mainPort);
            try {
                join.channel.register(boot.selector, OPS, join);
                System.out.println("server to join " + join);
                boot.cacheChannel.handlerJoinChannel(join);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }
}
