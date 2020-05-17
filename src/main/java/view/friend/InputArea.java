package view.friend;

import view.common.SimpleScrollBarUI;

import javax.swing.*;
import java.awt.*;

import static view.common.ViewConstance.BORDER;
import static view.common.ViewConstance.WHITE;

/**
 * @author 88382571
 * 2019/5/15
 */
public class InputArea extends JTextArea {
     InputArea(UserView parent, Dimension dimension) {
        //自动换行
        setLineWrap(true);
        //不切断词
        setWrapStyleWord(true);

        SimpleScrollBarUI.scroll(this, parent, dimension);

        //字号
        setFont(new Font(Font.DIALOG, Font.ITALIC, 20));
        //字色
        setForeground(Color.BLACK);
        //无边框
        setBorder(BORDER);
        setBackground(WHITE);
    }

}
