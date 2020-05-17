package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class TestDemo2 {
    private static final int OPS = SelectionKey.OP_READ;

    public static void main(String[] args) throws IOException, InterruptedException {
        //占用端口
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(new InetSocketAddress(6700), 1000);
        socketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        //自身开始接受注册服务
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    SocketChannel channel;
                    SelectableChannel selectableChannel = key.channel();
                    //服务端监听创建新通道
                    if (selectableChannel instanceof ServerSocketChannel) {
                        channel = ((ServerSocketChannel) selectableChannel).accept();
                        channel.configureBlocking(false);
                        channel.register(selector, OPS);
                        System.out.println("register " + channel.getRemoteAddress());
                    } else if (selectableChannel instanceof SocketChannel) {
                        channel = (SocketChannel) selectableChannel;
                    } else {
                        //未知通道
                        throw new RuntimeException("not support this selectableChannel:" + selectableChannel.getClass());
                    }
                    if (key.isReadable()) {
                        try {
                            giveData(channel);
                        } catch (IOException e) {
                            channel.close();
                            l = 0;
                            e.printStackTrace();
                        }
                    }
                }
                selectionKeys.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(100 * 1024 * 1024);
    private static int l = 0;

    private static void giveData(SocketChannel channel) throws IOException {
        buffer.clear();
        //读buffer
        while (true) {
            int read = channel.read(buffer);
            System.out.println(read);
            if (read == 0 || buffer.position() == buffer.limit()) {
                break;
            } else if (read == -1) {
                channel.close();
            }
        }
        System.out.println("out "+l);
        //buffer改为写模式
        buffer.flip();
        int limit = buffer.limit();
        if (limit != 0) {
            byte[] data = new byte[limit];
            buffer.get(data);
            l += data.length;
        }
    }

}
