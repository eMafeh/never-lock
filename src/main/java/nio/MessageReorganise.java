package nio;

import common.util.ThreadUtil;
import nio.message.GetMessage;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class MessageReorganise {
    final ThreadPoolExecutor Executor = ThreadUtil.createPool(4, 8, "msg-consumer-");
    final LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    long count = 0;
    int length;
    final byte[] prefix = new byte[5];
    final BaseChannelProxy proxy;
    /**
     * 队列第一个
     */
    private byte[] next;

    /**
     * 第一个的下标
     */
    int index;
    int rpcService;

    public MessageReorganise(final BaseChannelProxy baseChannelProxy) {
        this.proxy = baseChannelProxy;
    }

    public void receive(final byte[] data) {
        //队列里加入新的data
        queue.add(data);
        //记录当前总长度
        count += data.length;
        receive();
    }

    private void receive() {
        //获取prefix
        if (length <= 0) {
            int pl = prefix.length;
            if (count < pl) {
                return;
            }
            //去报文头长度
            count -= pl
            ;
            for (int i = 0; i < pl
                    ; i++) {
                while (next == null || next.length <= index) {
                    next = queue.remove();
                    index = 0;
                }
                prefix[i] = next[index++];
            }
            this.length = RpcService.getLength(prefix);
            this.rpcService = prefix[4];
        }
        //取报文体
        if (length > 0 && count >= length) {
            //取 length 报文长度
            count -= length;
            byte[] result = new byte[length];
            int strIndex = 0;
            while (strIndex < length) {
                while (next == null || next.length <= length) {
                    next = queue.remove();
                    index = 0;

                }
                int nextDl = next.length - index;
                int need = length - strIndex;
                int dl = Math.min(need, nextDl);
                System.arraycopy(next, index, result, strIndex
                        , dl);
                index += dl;
                strIndex += dl;

            }
            length = -1;
            int i = this.rpcService;
            Executor.execute(() -> proxy.serverBoot.service(proxy, new GetMessage(proxy.user, i, result)));
            receive();
        }
    }
}
