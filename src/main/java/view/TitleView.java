package view;

import view.friend.MsbKeChengBiaoMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.function.BiConsumer;

import static view.common.ViewConstance.*;

/**
 * @author 88382571
 * 2019/5/6
 */
class TitleView extends JMenuBar {
    TitleView(String name, Runnable hidden, Runnable close, BiConsumer<Integer, Integer> move) {
        setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        setPreferredSize(new Dimension(WIDTH, LINE_HEIGHT));
        setBackground(Color.WHITE);
        setBorder(BORDER);

        JTextArea title = new JTextArea(name);
        title.setEnabled(false);
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 15));

        JButton h = button(hidden, HIDDEN);
        JButton c = button(close, CLOSE);

        add(title);
        add(new MsbKeChengBiaoMenu());
        add(h);
        add(c);

        //拖拽移动
        MouseMotionListener listener = new MouseMotionListener() {
            int xx;
            int yy;
            long last;

            @Override
            public void mouseDragged(MouseEvent e) {
                long when = e.getWhen();
                if (when - last > 100) {
                    xx = e.getX();
                    yy = e.getY();
                }
                last = when;
                move.accept(e.getX() - xx, e.getY() - yy);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        };
        addMouseMotionListener(listener);
        title.addMouseMotionListener(listener);

    }

    private JButton button(Runnable runnable, Image image) {
        Action hidden = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        };
        hidden.putValue(Action.SMALL_ICON, new ImageIcon(image));
        JButton h = new JButton(hidden);
        h.setBackground(Color.white);
        h.setFocusPainted(false);
        return h;
    }
}
