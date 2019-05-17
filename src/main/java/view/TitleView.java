package view;

import view.common.MyButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.function.BiConsumer;

import static view.common.ViewConstance.*;

public class TitleView {
    static JPanel titlePanel(String name, Runnable close, Runnable hidden, BiConsumer<Integer, Integer> move) {
        JPanel title = new JPanel();
        title.setPreferredSize(new Dimension(WIDTH, LINE_HEIGHT));
        title.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        title.setBackground(Color.white);
        JTextArea textArea = new JTextArea(name);
        textArea.setPreferredSize(new Dimension(WIDTH - 2 * BUTTON_WIDTH, LINE_HEIGHT));
        textArea.setEnabled(false);
        textArea.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
        textArea.addMouseMotionListener(new MouseMotionListener() {
            int xx;
            int yy;
            long last;

            @Override
            public void mouseDragged(final MouseEvent e) {
                long when = e.getWhen();
                if (when - last > 100) {
                    xx = e.getX();
                    yy = e.getY();
                }
                last = when;
                move.accept(e.getX() - xx, e.getY() - yy);
            }

            @Override
            public void mouseMoved(final MouseEvent e) {

            }
        });
        title.add(textArea);
        Color color = new Color(229, 131, 114);
        MyButton button = new MyButton("x", color, color, color, "title", (a, b) -> close.run());
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        title.add(button);


        color = new Color(60, 63, 65);
        button = new MyButton("-", color, color, color, "title", (a, b) -> hidden.run());
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        title.add(button);
        return title;
    }
}
