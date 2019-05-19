package view;

import dto.TransMsg;
import dto.UserDto;
import nio.core.User;
import service.TransMsgService;
import view.common.ConstListener;
import view.common.MyTextArea;
import view.common.SimpleScrollBarUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import static view.common.ViewConstance.*;

public class ViewFriend {
    private final User user;
    private final ViewMain viewMain;
    private final JPanel right = new JPanel();
    private final UserButton left;
    private final MyTextArea input = new MyTextArea();
    private final MyTextArea history = new MyTextArea();
    private final JScrollPane historyScroll;

    public ViewFriend(final ViewMain viewMain, final User user) {
        this.viewMain = viewMain;
        this.user = user;
        JPanel msgView = viewMain.msgView;

        msgView.add(user.uniqueIdentifier(), right);
        right.setPreferredSize(msgView.getPreferredSize());
        historyScroll = history();
        input();

        left = new UserButton(viewMain, user);
        Consumer<String> msgListen = msg -> {
            if (msg != null) {
                addHistory(msg);
                viewMain.root.show();
            }
            viewMain.friendsView.remove(left);
            viewMain.friendsView.add(left, 0);
            viewMain.friendsView.revalidate();
            left.doClick();
        };
        msgListen.accept(null);
        user.listenGetMsg.add(msgListen);

    }

    private void addHistory(final String msg) {
        history.append(msg + "\n");
        history.validate();
        JScrollBar bar = historyScroll.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    private void input() {
        input.addFocusListener(ConstListener.FOCUS_LISTENER);
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    JTextArea textArea = (JTextArea) e.getComponent();
                    String text = textArea.getText();
                    textArea.setText("");
                    e.consume();
                    addHistory(text);
                    if (user.equals(viewMain.root.self)) {

                    } else {
                        viewMain.root.leader.sendMsg(TransMsgService.class, new TransMsg(new UserDto(user), text), Integer.MAX_VALUE);
                    }
                }
            }
        });
        SimpleScrollBarUI.scroll(input, right, new Dimension(RIGHT_WIDTH, INPUT_HEIGHT));
    }

    private JScrollPane history() {
        history.setEnabled(false);
        return SimpleScrollBarUI.scroll(history, right, new Dimension(RIGHT_WIDTH, HISTORY_HEIGHT));
    }
}
