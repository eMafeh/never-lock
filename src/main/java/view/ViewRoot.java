package view;

import common.util.ThreadUtil;
import nio.core.User;
import view.common.MyIconImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static common.RobotHandler.*;
import static view.common.ViewConstance.*;

public class ViewRoot {
    private final JFrame ROOT = new JFrame();
    private final ViewMain viewMain;
    final User self;
    final User leader;

    private volatile long hiddenTime = Long.MAX_VALUE;
    private final Thread HIDDEN = ThreadUtil.createLoopThread(() -> {
        try {
            long l = hiddenTime - System.currentTimeMillis();
            if (l > 0) {
                Thread.sleep(l);
            } else {
                hiddenTime = Long.MAX_VALUE;
                hidden();
            }
        } catch (InterruptedException e) {
        }
    }, "hidden-view");


    public ViewRoot(final String name, final String iconPath, final User leader, final User self) {
        this.leader = leader;
        this.self = self;
        this.viewMain = new ViewMain(this);
        ROOT.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        ROOT.setAlwaysOnTop(true);
        ROOT.setBounds((X_MAX - WIDTH) / 2, (Y_MAX - HEIGHT) / 2, WIDTH, HEIGHT);
        ROOT.setUndecorated(true);
        ROOT.setOpacity(0.75F);


        JPanel titlePanel;
        try {
            BufferedImage read = MyIconImage.get(iconPath);
            TrayIcon icon = new TrayIcon(read, name, new PopupMenu());
            icon.setImageAutoSize(true);
            icon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        show();
                    }
                }
            });
            ROOT.setIconImage(read);
            SystemTray systemTray = SystemTray.getSystemTray();
            systemTray.add(icon);
            titlePanel = TitleView.titlePanel(name, () -> {
                systemTray.remove(icon);
                System.exit(0);
            }, this::hidden, (dx, dy) -> {
                Point location = ROOT.getLocation();
                ROOT.setLocation(location.x + dx, location.y + dy);

            });
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        ROOT.add(titlePanel);
        ROOT.add(viewMain.getMain());
        show();
        HIDDEN.start();
        viewMain.createUser(leader);
        if (!leader.equals(self)) {
            viewMain.createUser(self);
        }
        User.LISTEN_CREATE.add(viewMain::createUser);

    }

    private void hidden() {
        ROOT.setVisible(false);
    }

    void show() {
        ROOT.setVisible(true);
    }

    public void show(final String msg, final int time) {
        self.newMsg(msg);
        hiddenTime = System.currentTimeMillis() + time * 1000;
        HIDDEN.interrupt();
    }
}
