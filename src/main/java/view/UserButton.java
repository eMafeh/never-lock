package view;

import common.util.StaticBeanFactory;
import nio.core.User;
import view.common.LabelBtn;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.IntConsumer;

import static view.common.ViewConstance.*;

/**
 * @author 88382571
 * 2019/5/15
 */
class UserButton extends JPanel {
    private final LabelBtn button;
    private final JTextField change;

    UserButton(User user) {
        ViewMain viewMain = StaticBeanFactory.get(ViewMain.class);
        change = new JTextField();

        CardLayout mgr = new CardLayout(0, 0);
        setLayout(mgr);

        CardLayout cardLayout = (CardLayout) viewMain.msgView.getLayout();
        final UserButton that = this;
        button = new LabelBtn(user.getNotNullName(), LEAVE, HOVER, CHOSE, "friend", (btn, e) -> {
            cardLayout.show(viewMain.msgView, user.uniqueIdentifier());
            viewMain.msgView.revalidate();
            boolean supportUser = user == User.SELF || !user.windows;
            if (supportUser && e != null && e.getClickCount() == 2) {
                mgr.show(that, "change");
                change.setText(user.getNotNullName());
                change.requestFocus();
            }
        });
        button.setToolTipText(user.uniqueIdentifier());
        change.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                boolean submit = keyCode == KeyEvent.VK_ENTER;
                if (submit || keyCode == KeyEvent.VK_ESCAPE) {
                    if (submit) {
                        user.setName(((JTextComponent) e.getComponent()).getText());
                    }
                    mgr.show(that, "button");
                    e.consume();
                }
            }
        });
        add("change", change);
        add("button", button);
        mgr.show(that, "button");

        //样式
        setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
        setForeground(Color.BLACK);
        setPreferredSize(new Dimension(USER_WIDTH, USER_HEIGHT));
        button.setFont(getFont());
        button.setForeground(getForeground());
        button.setPreferredSize(getPreferredSize());
        change.setFont(new Font(Font.DIALOG, Font.ITALIC, 15));
        change.setForeground(getForeground());
        change.setPreferredSize(getPreferredSize());
        change.setBorder(BORDER);
        change.setBackground(Color.WHITE);
        change.setHorizontalAlignment(JTextField.CENTER);

        //监听用户名更改事件
        user.listenName.add(button::setText);

        if (User.SELF != user) {
            //监听用户状态更改
            IntConsumer statusListener;
            statusListener = user.windows ? status -> {
                if (status == 1) {
                    button.icon(ONLINE, 33);
                } else {
                    button.icon(OFFLINE, 27);
                }
            } : status -> {
                if (status == 1) {
                    button.icon(LINK, 25);

                } else if (status == 0) {
                    button.icon(UNLINK, 20);
                } else {
                    button.icon(REMOVE, 20);
                }
            };
            statusListener.accept(user.getStatus());
            user.listenStatus.add(statusListener);
        }
    }

    void doClick() {
        button.colorClick.accept(null);
    }
}
