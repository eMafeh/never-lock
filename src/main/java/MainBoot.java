import common.CmdInstructions;
import common.util.DestroyedUtil;
import common.util.SystemUtil;
import common.util.ThreadUtil;
import nio.ServerBoot;
import nio.core.User;
import service.HelloService;
import service.ProxyMsgService;
import service.TransMsgService;
import service.UpdateUser;
import source.PropertiesHandler;
import view.ViewRoot;

import java.io.IOException;
import java.time.LocalTime;

public class MainBoot {
    static {
        DestroyedUtil.addListener(() -> {
        });
    }

    public static void main(String[] args) throws IOException {
        User leader = User.getUser(SystemUtil.IP, 8988);
        User self = User.getUser(SystemUtil.IP, 8988);


        view(leader, self);

        new HelloService();
        new ProxyMsgService();
        new TransMsgService();
        new UpdateUser();
        new ServerBoot(leader, self);
    }

    private static void view(final User leader, final User self) {
        if (!SystemUtil.LOCAL) {
            return;
        }
        System.out.println("swing ui init");
        ViewRoot viewRoot = new ViewRoot("防锁定1.0.0", "icon.png", leader, self);
        PropertiesHandler.init();
        ThreadUtil.createLoopThread(() -> {
            try {
                NeverLock.unlock();
                LocalTime now = LocalTime.now();
                tryNode(now.getHour(), now.getMinute(), viewRoot);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, "never-lock")
                .start();
    }

    private static void tryNode(final int hour, final int minute, final ViewRoot viewRoot) throws IOException {
        PropertiesHandler.TimeNode timeNode = PropertiesHandler.USE_NODES[hour][minute];
        if (timeNode != null) {
            boolean isFirst = timeNode != lastTimeNode;
            if (timeNode.lock) {
                if (timeNode.loop || isFirst) {
                    System.out.println(timeNode);
                    CmdInstructions.lock();
                }
            } else if (isFirst) {
                System.out.println(timeNode);
                viewRoot.show(timeNode.getMsg(), timeNode.time);
            }
        }
        lastTimeNode = timeNode;
    }

    private static volatile PropertiesHandler.TimeNode lastTimeNode;
}
