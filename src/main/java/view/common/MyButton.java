package view.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static view.common.ViewConstance.BORDER;

public class MyButton extends JLabel {
    private static final Map<String, MyButton[]> BUTTON_GROUP = new ConcurrentHashMap<>();
    public final Consumer<MouseEvent> colorClick;

    public MyButton(String text, Color leave, Color hover, Color chose, String group, BiConsumer<MyButton, MouseEvent> click) {
        super(text, null, CENTER);
        MyButton[] now = BUTTON_GROUP.computeIfAbsent(group, a -> new MyButton[1]);
        setOpaque(true);

        setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        setForeground(Color.WHITE);

        setBackground(leave);

        final MyButton that = this;

        colorClick = e -> {
            click.accept(that, e);
            MyButton old = now[0];
            if (old == that) {
                return;
            }
            setBackground(chose);
            if (old != null) {
                old.setBackground(leave);
            }
            now[0] = that;
        };


        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                that.colorClick.accept(e);
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                if (that != now[0]) {
                    setBackground(hover);
                }
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                if (that != now[0]) {
                    setBackground(leave);
                }
            }
        });
        setFocusable(false);
        setBorder(BORDER);
    }

    public void icon(Image image, int iconSize) {
        setIcon(new ImageIcon(image.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)));
    }
}
