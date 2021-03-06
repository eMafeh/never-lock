package view.friend;

import common.util.ExceptionUtil;
import nio.core.User;
import view.common.ConstListener;
import view.common.MyTransferHandler;
import view.common.ScreenshotAction;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.UUID;

import static view.common.ViewConstance.*;

public class UserView extends JPanel {
    /**
     * 历史信息
     */
    public final HistoryArea history;
    /**
     * 工具栏
     */
    public final UserBar userBar;
    /**
     * 输入框
     */
    public final InputArea input;
    public final User user;

    public UserView(User user, MyTransferHandler newHandler) {
        this.user = user;
        history = new HistoryArea(this, newHandler);
        userBar = new UserBar(this);
        input = new InputArea(this, new Dimension(RIGHT_WIDTH, INPUT_HEIGHT));
        userBar.add(new ScreenshotButton(this));
        userBar.add(new ClearButton(this));
        input.addFocusListener(ConstListener.FOCUS_WHITE);
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }

                JTextArea textArea = (JTextArea) e.getComponent();
                String text = textArea.getText();
                textArea.setText("");
                e.consume();
                history.addMsg(text, true);
                //不给自身发信息
                if (user == User.SELF) {
                    String prefix = "removeremotelinux";
                    if (text.startsWith(prefix)) {
                        try {
                            String[] split = text.split(":");
                            User linux = User.getUser(split[1], Integer.parseInt(split[2]), true);
                            if (linux.getStatus() == 0) {
                                //通知其他用户新用户
                                linux.setStatus(-1);
                            }
                        } catch (Exception exc) {
                            ExceptionUtil.print(exc);
                        }
                    } else {
                        user.newMsg(new StringBuilder(text).reverse()
                                .toString());
                    }
                } else {
                    if (!user.windows && "clear".equalsIgnoreCase(text.trim())) {
                        history.removeAll();
                    } else {
                        user.sendMsg(text);
                    }
                }
            }
        });
    }
}
