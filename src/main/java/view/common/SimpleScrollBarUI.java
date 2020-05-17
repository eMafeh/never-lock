package view.common;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

import static view.common.ViewConstance.BORDER;

/**
 * @author 88382571
 * 2019/5/7
 */
public class SimpleScrollBarUI extends BasicScrollBarUI {
    private SimpleScrollBarUI() {
    }

    public static JScrollPane scroll(Component view, Container parent, Dimension dimension) {
        //滚动布局包装
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setPreferredSize(dimension);
        scrollPane.setBorder(BORDER);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //滚动布局ui
        JScrollBar bar = scrollPane.getVerticalScrollBar();
        bar.setBorder(BORDER);
        bar.setUI(new SimpleScrollBarUI());
        bar.setUnitIncrement(40);
        parent.add(scrollPane);
        return scrollPane;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        c.setPreferredSize(new Dimension(8, 0));
        return super.getPreferredSize(c);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        g.translate(thumbBounds.x, thumbBounds.y);
        g.setColor(Color.gray);
        g.fillRoundRect(0, 0, 8, thumbBounds.height - 1, 0, 0);
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createDecreaseButton(orientation);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton jButton = new JButton();
        jButton.setBorderPainted(false);
        jButton.setContentAreaFilled(false);
        jButton.setBorder(null);
        return jButton;
    }

}
