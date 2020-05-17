package view.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.awt.Image.SCALE_SMOOTH;
import static view.common.ViewConstance.BORDER;

/**
 * @author 88382571
 * 2019/5/14
 */
public class LabelBtn extends JLabel {
    private static final Map<String, LabelBtn[]> BUTTON_GROUP = new ConcurrentHashMap<>();

    public final Consumer<MouseEvent> colorClick;

    public LabelBtn(String text, Color leave, Color hover, Color chose, String group,
                    BiConsumer<LabelBtn, MouseEvent> click) {
        super(text, null, CENTER);
        LabelBtn[] now = BUTTON_GROUP.computeIfAbsent(group, a -> new LabelBtn[1]);
        setOpaque(true);
        //字体
        setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        //字体颜色
        setForeground(Color.WHITE);
        //背景色
        setBackground(leave);
        final LabelBtn that = this;
        colorClick = e -> {
            click.accept(that, e);
            LabelBtn old = now[0];
            if (old == that) {
                return;
            }
            setBackground(chose);
            if (old != null) {
                old.setBackground(leave);
            }
            now[0] = that;
        };
        //事件
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                that.colorClick.accept(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (that != now[0]) {
                    setBackground(hover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (that != now[0]) {
                    setBackground(leave);
                }
            }
        });
        //不可选中内容
        setFocusable(false);
        //无边界
        setBorder(BORDER);
    }

    public void icon(Image image, int iconSize) {
        setIcon(new ImageIcon(image.getScaledInstance(iconSize, iconSize, SCALE_SMOOTH)));
    }
}
