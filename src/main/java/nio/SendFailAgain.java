package nio;

import common.util.ThreadUtil;
import nio.message.SendMessage;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class SendFailAgain {
    private final PriorityBlockingQueue<SendMessage> SEND_FAIL;
    final Thread send_again_thread;

    public SendFailAgain(final ServerBoot boot) {
        SEND_FAIL = new PriorityBlockingQueue<>(3, Comparator.comparingLong(msg -> msg.lastTime));
        send_again_thread = ThreadUtil.createLoopThread(() -> {
            SendMessage msg = null;
            try {
                msg = SEND_FAIL.take();
                long dTime = msg.lastTime - System.currentTimeMillis();
                if (dTime > 0) {
                    Thread.sleep(dTime);
                }
                boot.send(msg);
            } catch (InterruptedException e) {
                if (msg != null) {
                    SEND_FAIL.put(msg);
                }
            }
        }, "send-fail");
        send_again_thread.start();
    }

    public void tryAgain(final SendMessage msg) {
        if (msg.isUpdateUser()) {
            return;
        }
        if (msg.again > 0) {
            msg.again--;
            long now = System.currentTimeMillis();
            if (msg.sleepTime < 60000) {
                msg.sleepTime += 1000;
            }
            long lastTime = msg.lastTime;
            if (lastTime == 0) {
                msg.lastTime = now + msg.sleepTime;

            } else {
                msg.lastTime += msg.sleepTime;
            }
            SEND_FAIL.put(msg);
            send_again_thread.interrupt();
        }
    }
}
