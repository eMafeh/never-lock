package view;

import common.util.ExceptionUtil;
import common.util.StaticBeanFactory;
import common.util.ThreadUtil;
import nio.core.User;
import unlock.PropertiesHandler;
import view.common.MyIconImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static common.RobotHandler.X_MAX;
import static common.RobotHandler.Y_MAX;
import static view.common.ViewConstance.HEIGHT;
import static view.common.ViewConstance.WIDTH;

/**
 * @author 88382571
 * 2019/5/6
 */
public class ViewRoot {
    private final JFrame ROOT = new JFrame();

    private volatile long hiddenTime = Long.MAX_VALUE;
    private final Thread HIDDEN = ThreadUtil.createLoopThread(() -> {
        try {
            long l = hiddenTime - System.currentTimeMillis();
            if (l > 0) {
                Thread.sleep(l);
            } else {
                hiddenTime = Long.MAX_VALUE;
                ROOT.setVisible(false);
            }
        } catch (InterruptedException e) {
        }
    }, "view-hidden");


    public ViewRoot(String name, String iconPath) {
        ExceptionUtil.consumer = User.SELF::newMsg;
        StaticBeanFactory.put(ViewRoot.class, this);
        ViewMain viewMain = new ViewMain();
        //简单布局
        ROOT.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        //总是顶层显示
        ROOT.setAlwaysOnTop(true);
        //默认居中
        ROOT.setBounds((X_MAX - WIDTH) / 2, (Y_MAX - HEIGHT) / 2, WIDTH, HEIGHT);
        //去除标题框且整体透明度降低 顺序不可颠倒
        ROOT.setUndecorated(true);
        ROOT.setOpacity(0.75f);
        PropertiesHandler.LISTEN_CHANGE_PRO.put("Opacity", value -> {
            float opacity = Float.parseFloat(value);
            ROOT.setOpacity(opacity);
        });
        try {
            BufferedImage image = MyIconImage.get(iconPath);
            //监听模式 匿名内部类 ——Swing应用，创建通知区域图标，赋予双击事件
            //创建一个通知区域图标
            TrayIcon icon = new TrayIcon(image, name, new PopupMenu());
            icon.setImageAutoSize(true);
            //图标增加鼠标点击监听者
            icon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //当鼠标的连击次数为2
                    if (e.getClickCount() == 2) {
                        //显示窗口
                        ROOT.setVisible(true);
                    }
                }
            });
            ROOT.setIconImage(image);
            SystemTray systemTray = SystemTray.getSystemTray();
            systemTray.add(icon);

            JMenuBar title = new TitleView(name, () -> ROOT.setVisible(false), () -> {
                systemTray.remove(icon);
                System.exit(0);
            }, (dx, dy) -> {
                Point location = ROOT.getLocation();
                ROOT.setLocation(location.x + dx, location.y + dy);
            });
            ROOT.setJMenuBar(title);
        } catch (AWTException e) {
            ExceptionUtil.throwT(e);
        }
        ROOT.add(viewMain.getMain());
        ROOT.setVisible(true);
        HIDDEN.start();

        User.init(viewMain::createUser, false);
    }

    public void show(String msg, int time) {
        User.SELF.newMsg(msg);
        hiddenTime = System.currentTimeMillis() + time * 1000;
        HIDDEN.interrupt();
    }
}
