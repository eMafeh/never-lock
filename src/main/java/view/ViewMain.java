package view;

import nio.core.User;
import view.common.SimpleScrollBarUI;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

import static view.common.ViewConstance.*;

public class ViewMain {
    final ViewRoot root;
    final JPanel main = new JPanel();
    final JPanel friendsView = new JPanel();
    final JPanel msgView = new JPanel();
    private final AtomicInteger userSize = new AtomicInteger();

    public ViewMain(final ViewRoot root) {
        this.root = root;
        main.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        friendsView.setBorder(BORDER);
        friendsView.setLayout(new GridLayout(MAIN_HEIGHT / USER_HEIGHT, 1));
        friendsView.setBackground(LEAVE);
        SimpleScrollBarUI.scroll(friendsView, main, new Dimension(USER_WIDTH, MAIN_HEIGHT));
        msgView.setBorder(BORDER);
        msgView.setLayout(new CardLayout(0, 0));
        msgView.setPreferredSize(new Dimension(RIGHT_WIDTH, MAIN_HEIGHT));
        main.add(msgView);
    }

    public void createUser(final User user) {
        GridLayout layout = (GridLayout) friendsView.getLayout();
        int size = userSize.incrementAndGet();
        int rows = Math.max(MAIN_HEIGHT / USER_HEIGHT, size);
        layout.setRows(rows);
        friendsView.setPreferredSize(new Dimension(USER_WIDTH, rows * USER_HEIGHT));
        new ViewFriend(this, user);
    }

    public JPanel getMain() {
        return main;
    }
}
