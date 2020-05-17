package test;

import common.util.ProxyUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestDemo {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("localhost", 6700));
        for (int i = 0; i < 10; i++) {
            if (channel.isConnectionPending()) {
                if (channel.finishConnect()) {
                    byte[] array = Files.readAllBytes(Paths.get("E:\\FeigeDownload\\ideaIU-2016.3.1.exe"));
                    System.out.println(array.length);
                    ByteBuffer wrap = ByteBuffer.wrap(array);
                    ProxyUtil.FieldProxy<Object> writeLock = ProxyUtil.getField(Class.forName("sun.nio.ch.SocketChannelImpl"), "writeLock");
                    synchronized (writeLock.get(channel)) {
                        long l = System.currentTimeMillis();
                        while (wrap.position() != wrap.limit()) {
                            channel.write(wrap);
                        }
                        System.out.println(l - System.currentTimeMillis());
                    }
                }
            }
            Thread.sleep(100L);
        }
        Thread.sleep(10000L);
        channel.close();
    }
}
