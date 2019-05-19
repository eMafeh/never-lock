package view;

import nio.CacheChannel;
import nio.core.User;
import view.common.MyButton;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.IntConsumer;

import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static view.common.ViewConstance.*;

public class UserButton extends JPanel {
    private final MyButton button;
    private final JTextField change;

    UserButton(ViewMain main, User user) {
        final boolean isSelf = user.equals(main.root.self);
        change = new JTextField();

        CardLayout cardLayout = new CardLayout(0, 0);
        setLayout(cardLayout);

        CardLayout layout = (CardLayout) main.msgView.getLayout();
        UserButton that = this;
        button = new MyButton(user.getNotNullName(), LEAVE, HOVER, CHOSE, "friend", (btn, e) -> {
            layout.show(main.msgView, user.uniqueIdentifier());
            if (isSelf && e != null && e.getClickCount() == 2) {
                cardLayout.show(that, "change");
                change.setText(user.getNotNullName());
                change.requestFocus();
            }
        });
        button.setToolTipText(user.uniqueIdentifier());
        change.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                int keyCode = e.getKeyCode();
                boolean submit = keyCode == VK_ENTER;
                if (submit || keyCode == VK_ESCAPE) {
                    if (submit) {
                        main.root.leader.sendMsg(CacheChannel.NewNameService.class, ((JTextComponent) e.getComponent()).getText(), Integer.MAX_VALUE);
                    }
                    cardLayout.show(that, "button");
                    e.consume();
                }
            }
        });
        add("change", change);
        add("button", button);
        cardLayout.show(that, "button");


        setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
        setForeground(Color.black);
        setPreferredSize(new Dimension(USER_WIDTH, USER_HEIGHT));


        button.setFont(getFont());
        button.setForeground(getForeground());
        button.setPreferredSize(getPreferredSize());

        change.setFont(getFont());
        change.setForeground(getForeground());
        change.setPreferredSize(getPreferredSize());
        change.setBorder(BORDER);
        change.setBackground(Color.white);
        change.setHorizontalAlignment(SwingConstants.CENTER);

        user.listenName.add(button::setText);

        if (!isSelf) {
            IntConsumer intConsumer = status -> button.icon(status == 0 ? OFFLINE : ONLINE, 30);
            intConsumer.accept(user.getStatus());
            user.listenStatus.add(intConsumer);
        }

    }

    public void doClick() {
        button.colorClick.accept(null);
    }
}
