package view.common;

import common.util.ExceptionUtil;

import javax.swing.*;
import java.awt.*;

public class MyLiLayOut implements LayoutManager {
    private final JScrollPane scroll;
    private final Dimension dimension;
    private final Component target;


    public MyLiLayOut(Component target, Container parent, Dimension dimension) {
        /* 信息可滚动 */
        scroll = SimpleScrollBarUI.scroll(target, parent, dimension);
        this.dimension = dimension;
        this.target = target;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    private Dimension size(Container target) {
        checkTarget(target);
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(dimension.width, 0);
            int nmembers = target.getComponentCount();
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = getSize(m);
                    dim.height += d.height;
                }
            }
            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;
            return dim;
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return size(target);
    }


    @Override
    public Dimension minimumLayoutSize(Container target) {
        return size(target);
    }

    @Override
    public void layoutContainer(Container target) {
        checkTarget(target);
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int y = insets.top;
            for (int i = 0; i < target.getComponentCount(); i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = getSize(m);
                    m.setSize(d);
                    m.setLocation(insets.left, y);
                    y += d.height;
                }
            }
            scroll.getVerticalScrollBar()
                    .setValue(y);
        }
    }

    private Dimension getSize(Component m) {
        Dimension preferredSize = m.getPreferredSize();
        return new Dimension(preferredSize.width, preferredSize.height);
    }

    private void checkTarget(Container target) {
        ExceptionUtil.isTrue(target == this.target);
    }
}
