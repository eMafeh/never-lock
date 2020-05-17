package view;

import common.util.StaticBeanFactory;
import nio.core.User;
import view.common.MyTransferHandler;
import view.friend.UserView;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * @author 88382571
 * 2019/5/9
 */
class ViewFriend {
    ViewFriend(User user) {
        ViewMain viewMain = StaticBeanFactory.get(ViewMain.class);
        JPanel msgView = viewMain.msgView;
        MyTransferHandler newHandler = new MyTransferHandler(file -> {
            user.newFile(file);
            if (user != User.SELF) {
                user.sendFile(file);
            }
        });
        /* 左侧单个用户 */
        final UserButton left = new UserButton(user);
        left.setTransferHandler(newHandler);
        /* 右侧单个卡片 */
        final UserView right = new UserView(user, newHandler);
        //增加卡片 调整卡片大小
        msgView.add(user.uniqueIdentifier(), right);
        right.setPreferredSize(msgView.getPreferredSize());

        //监听接收信息
        Consumer<String> messageListener = msg -> {
            if (msg != null) {
                right.history.addMsg(msg, false);
                SwingUtilities.getRoot(right)
                        .setVisible(true);
            }
            viewMain.friendsView.remove(left);
            viewMain.friendsView.add(left, 0);
            //聊天内容唤出
            left.doClick();
        };
        messageListener.accept(null);
        user.listenGetMsg.add(messageListener);

        user.listenGetFile.add(file -> {
            if (file != null) {
                right.history.addPic(file);
                SwingUtilities.getRoot(right)
                        .setVisible(true);
            }
            viewMain.friendsView.remove(left);
            viewMain.friendsView.add(left, 0);
            //聊天内容唤出
            left.doClick();
        });
    }
}
