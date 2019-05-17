package view.common;

import javax.swing.*;
import java.awt.*;

import static view.common.ViewConstance.BORDER;
import static view.common.ViewConstance.WHITE;

public class MyTextArea extends JTextArea {
    public MyTextArea() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setFont(new Font(Font.DIALOG, Font.ITALIC, 20));
        setForeground(Color.black);
        setBorder(BORDER);
        setBackground(WHITE);
    }
}
