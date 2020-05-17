package view.friend;

import javax.swing.*;
import java.awt.*;

import static view.common.ViewConstance.*;

class UserBar extends JMenuBar {

    public UserBar(UserView parent) {
        setPreferredSize(new Dimension(RIGHT_WIDTH, BUTTON_HEIGHT));
        //无边框
        setBorder(BORDER);
        setBackground(LEAVE);
//        add(new Jm)
        parent.add(this);
    }
}
