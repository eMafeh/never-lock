package view.friend;

import nio.core.User;
import view.common.ScreenshotAction;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static view.common.ViewConstance.LEAVE;

public class ScreenshotButton extends JButton {
    public ScreenshotButton(UserView userView) {
        AbstractAction action = new AbstractAction() {
            {
                putValue(Action.NAME, "截屏");
                putValue(Action.SHORT_DESCRIPTION, "捕捉屏幕");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                Component root = SwingUtilities.getRoot(userView.history);
                root.setVisible(false);
                ScreenshotAction.pre(image -> {
                    if (image != null) {
                        //写入本地文件
                        File file = new File("Downloads" + File.separator + UUID.randomUUID()
                                .toString() + ".png");
                        try {
                            File parentFile = file.getParentFile();
                            if (!parentFile.exists()) {
                                parentFile.mkdirs();
                            }
                            ImageIO.write(image, "png", file);
                            userView.user.newFile(file);
                            if (userView.user != User.SELF) {
                                userView.user.sendFile(file);
                            }
                        } catch (IOException e1) {
                            Arrays.stream(e1.getStackTrace())
                                    .forEach(element -> userView.history.addMsg(element.toString(), true));
                        }
                    }
                    root.setVisible(true);
                });
            }
        };
        setAction(action);
        userView.getActionMap()
                .put("screenshot", action);
        userView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke("alt A"), "screenshot");
        setBackground(LEAVE);
    }
}
