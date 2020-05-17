import common.util.DestroyedUtil;
import common.util.SystemUtil;
import nio.DbHandler;
import nio.ServerBoot;
import unlock.PropertiesHandler;
import view.ViewRoot;

import java.io.IOException;

/**
 * @author 88382571
 * 2019/4/22
 */
public class MainBoot {

    static {
        //		LogUtil.changeSystemOut("E:\\OCES\\temp good\\neverlock\\log.txt");
        DestroyedUtil.addListener(() -> {
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("server.main.port", "8989");

        if (SystemUtil.isWindows()) {
            //尝试创建 ui 界面 和防锁定功能
            System.out.println("swing ui init");
            new ViewRoot("防锁定1.5.6", "logo.png");
            PropertiesHandler.init();
        }
        //开启自身服务监听
        new ServerBoot();
        //持久化用户列表变化
        DbHandler.autoUpdate();
    }
}
