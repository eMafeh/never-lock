package view.common;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

import static view.common.ViewConstance.BORDER;

public class SimpleScrollBarUI extends BasicScrollBarUI {
    private SimpleScrollBarUI() {
    }

    public static JScrollPane scroll(Component view, Container parent, Dimension dimension) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setPreferredSize(dimension);
        scrollPane.setBorder(BORDER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setBorder(BORDER);
        scrollBar.setUI(new SimpleScrollBarUI());
        scrollBar.setUnitIncrement(40);
        parent.add(scrollPane);
        return scrollPane;
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        c.setPreferredSize(new Dimension(8, 0));
        return super.getPreferredSize(c);
    }

    @Override
    protected void paintThumb(final Graphics g, final JComponent c, final Rectangle thumbBounds) {
        g.translate(thumbBounds.x, thumbBounds.y);
        g.setColor(Color.gray);
        g.fillRoundRect(0, 0, 8, thumbBounds.height - 1, 0, 0);
    }

    @Override
    protected JButton createIncreaseButton(final int orientation) {
        return createDecreaseButton(orientation);
    }

    @Override
    protected JButton createDecreaseButton(final int orientation) {
        JButton jButton = new JButton();
        jButton.setBorderPainted(false);
        jButton.setContentAreaFilled(false);
        jButton.setBorder(null);
        return jButton;
    }
}
