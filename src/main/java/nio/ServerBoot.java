package nio;

import common.util.ExceptionUtil;
import common.util.StaticBeanFactory;
import common.util.ThreadUtil;
import dto.UserDto;
import nio.core.User;
import nio.message.MessageDto;
import nio.message.RemoteServicePackage;
import service.FileService;
import service.MsgService;
import service.UpdateUser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;
import java.util.Stack;


/**
 * @author 88382571
 * 2019/4/28
 */
public class ServerBoot {
    private static final int OPS = SelectionKey.OP_READ;
    private static final Stack<SocketChannel> WHILE_REGISTER_CHANNELS = new Stack<>();

    private final Selector selector;

    public ServerBoot() throws IOException {
        //占用端口
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(User.SELF.mainPort), 1000);
        socketChannel.configureBlocking(false);

        this.selector = Selector.open();
        StaticBeanFactory.put(ServerBoot.class, this);

        long count = DbHandler.read()
                .stream()
                .filter(userDto -> userDto.status != -1)
                .map(userDto -> userDto.getUser()
                        .setName(userDto.name))
                .filter(a -> a == User.SELF)
                .count();
        if (count == 1) {
            User.SELF.newMsg("欢迎登陆");
        } else {
            User.SELF.newMsg("欢迎首次登陆防锁定~~~~~");
            DbHandler.write();
        }

        //监听发送消息行为
        User.init(this::listen, false);

        //向其他节点注册
        ChannelHandler.init();

        //自身开始接受注册服务
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("register server port " + User.SELF.mainPort);

        //selector 开始监听
        ThreadUtil.createLoopThread(this::run, "server-selector")
                .start();
    }

    private void listen(User user) {
        //监听改名
        if (User.SELF == user || !user.windows) {
            user.listenName.add(name -> ChannelHandler.send(new RemoteServicePackage<>(null, UpdateUser.class, new UserDto(user))));
        }
        user.listenStatus.add(i -> {
            if (i == -1) {
                ChannelHandler.send(new RemoteServicePackage<>(null, UpdateUser.class, new UserDto(user)));
            }
        });
        user.listenSendFile.add(s -> FileService.send(user, s));
        user.listenSendMsg.add(s -> ChannelHandler.send(new RemoteServicePackage<>(user, MsgService.class, new MessageDto(s))));
        user.listenGetMsg.add(System.out::println);
    }

    private void run() {
        try {
            register();
            selector.select();
            register();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey key : selectionKeys) {
                handle(key);
            }
            selectionKeys.clear();
        } catch (Exception e) {
            ExceptionUtil.print(e);
        }
    }

    private void handle(SelectionKey key) throws IOException {
        SocketChannel channel;
        SelectableChannel selectableChannel = key.channel();
        //服务端监听创建新通道
        if (selectableChannel instanceof ServerSocketChannel) {
            channel = ((ServerSocketChannel) selectableChannel).accept();
            register0(channel);
        } else if (selectableChannel instanceof SocketChannel) {
            channel = (SocketChannel) selectableChannel;
        } else {
            //未知通道
            throw new RuntimeException("not support this selectableChannel:" + selectableChannel.getClass());
        }
        if (key.isReadable()) {
            MessageReorganise reorganise = (MessageReorganise) key.attachment();
            try {
                giveData(channel, reorganise);
            } catch (IOException e) {
                ChannelHandler.removeChannel(e, channel);
            }
        }
    }

    void register(SocketChannel channel) throws ClosedChannelException {
        WHILE_REGISTER_CHANNELS.push(channel);
        selector.wakeup();
    }

    private void register() throws IOException {
        while (!WHILE_REGISTER_CHANNELS.empty()) {
            register0(WHILE_REGISTER_CHANNELS.pop());
        }
    }

    private void register0(SocketChannel channel) throws IOException {
        channel.configureBlocking(false);
        channel.register(selector, OPS, new MessageReorganise(ServiceHandler.getMessageReorganise(channel)));
        System.out.println("register " + channel.getRemoteAddress());
    }

    private final ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);

    private void giveData(SocketChannel channel, MessageReorganise reorganise) throws IOException {
        buffer.clear();
        //读buffer
        while (true) {
            int read = channel.read(buffer);
            if (read == 0 || buffer.position() == buffer.limit()) {
                break;
            } else if (read == -1) {
                channel.close();
            }
        }
        //buffer改为写模式
        buffer.flip();
        int limit = buffer.limit();
        if (limit != 0) {
            byte[] data = new byte[limit];
            buffer.get(data);
            reorganise.receive(data);
        }
    }
}
