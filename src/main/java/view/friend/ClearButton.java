package view.friend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static view.common.ViewConstance.LEAVE;

public class ClearButton extends JButton {
    public ClearButton(UserView userView) {
        AbstractAction action = new AbstractAction() {
            {
                putValue(Action.NAME, "清空");
                putValue(Action.SHORT_DESCRIPTION, "清空聊天记录");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(() -> {
                    userView.history.removeAll();
                    userView.history.repaint();
                });
            }
        };
        setAction(action);
//        userView.getActionMap()
//                .put("clearHistory", action);
//        userView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
//                .put(KeyStroke.getKeyStroke("alt A"), "clearHistory");
        setBackground(LEAVE);
    }
}
