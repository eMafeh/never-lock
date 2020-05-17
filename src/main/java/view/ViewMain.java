package view;


import common.util.StaticBeanFactory;
import nio.core.User;
import view.common.SimpleScrollBarUI;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

import static view.common.ViewConstance.*;


/**
 * @author 88382571
 * 2019/5/6
 */
class ViewMain {
    /**
     * 全部
     */
    private final JPanel main = new JPanel();
    /**
     * 左侧
     */
    final JPanel friendsView = new JPanel();
    /**
     * 右侧
     */
    final JPanel msgView = new JPanel();
    /**
     * 单个用户的元素对象
     */
    private final AtomicInteger userSize = new AtomicInteger();

    ViewMain() {
        StaticBeanFactory.put(ViewMain.class, this);
        //左右两块布局
        main.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        //左边加入 单条网格布局
        friendsView.setBorder(BORDER);
        friendsView.setLayout(new GridLayout(MAIN_HEIGHT / USER_HEIGHT, 1));
        friendsView.setBackground(LEAVE);
        SimpleScrollBarUI.scroll(friendsView, main, new Dimension(USER_WIDTH, MAIN_HEIGHT));
        //右边加入 卡片布局
        msgView.setBorder(BORDER);
        msgView.setLayout(new CardLayout(0, 0));
        msgView.setPreferredSize(new Dimension(RIGHT_WIDTH, MAIN_HEIGHT));
        main.add(msgView);
    }

    void createUser(User user) {
        //刷新好友栏
        GridLayout layout = (GridLayout) friendsView.getLayout();
        int size = userSize.incrementAndGet();
        int rows = Math.max(MAIN_HEIGHT / USER_HEIGHT, size);
        layout.setRows(rows);
        friendsView.setPreferredSize(new Dimension(USER_WIDTH, rows * USER_HEIGHT));
        new ViewFriend(user);
    }

    JPanel getMain() {
        return main;
    }
}
