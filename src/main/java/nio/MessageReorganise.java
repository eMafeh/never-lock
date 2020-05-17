package nio;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

class MessageReorganise {
    private final LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private final Consumer<byte[]> consumer;
    private long count = 0;
    private int length;
    private final byte[] prefix = new byte[4];
    /**
     * 队列第一个
     */
    private byte[] next;
    /**
     * 第一个的下标
     */
    private int index;

    MessageReorganise(Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

    void receive(byte[] data) {
        //队列里加入新的data
        queue.add(data);
        //记录当前总长度
        count += data.length;
        receive();
    }

    private void receive() {
        //获取prefix
        if (length <= 0) {
            int pL = prefix.length;
            if (count < pL) {
                return;
            }
            //取报文头长度
            count -= pL;
            for (int i = 0; i < pL; i++) {
                while (next == null || next.length <= index) {
                    next = queue.remove();
                    index = 0;
                }
                prefix[i] = next[index++];
            }
            //报文长度
            this.length = RpcService.getLength(prefix);
        }
        //拼接str
        if (length > 0 && count >= length) {
            //取 length 报文长度
            count -= length;

            byte[] result = new byte[length];
            int strIndex = 0;
            while (strIndex < length) {
                while (next == null || next.length <= index) {
                    next = queue.remove();
                    index = 0;
                }

                int nextDl = next.length - index;
                int need = length - strIndex;
                int dl = Math.min(need, nextDl);
                System.arraycopy(next, index, result, strIndex, dl);
                index += dl;
                strIndex += dl;
            }
            length = -1;
            consumer.accept(result);
            receive();
        }
    }
}