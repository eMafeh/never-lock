package view.common;

import common.RobotHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.function.Consumer;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static view.common.ViewConstance.PEN;

public class ScreenshotAction {
    private static final JFrame FRAME;
    private static final Point START = new Point();
    private static final Point END = new Point();
    private static BufferedImage image;
    private static BufferedImage filter;
    private static Consumer<BufferedImage> consumer;

    static {
        FRAME = new JFrame();
        //简单布局
        FRAME.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        //总是顶层显示
        FRAME.setAlwaysOnTop(true);
        //默认居中
        FRAME.setBounds(0, 0, RobotHandler.X_MAX, RobotHandler.Y_MAX);
        //去除标题框且整体透明度降低 顺序不可颠倒
        FRAME.setUndecorated(true);
        FRAME.setOpacity(1);
        FRAME.setCursor(Toolkit.getDefaultToolkit()
                .createCustomCursor(PEN, new Point(0, 0), "knife"));
        JComponent all = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(filter, 0, 0, this);
                g.drawImage(image, START.x, START.y, END.x, END.y, START.x, START.y, END.x, END.y, null);
                g.setColor(Color.RED);
                g.drawRect(min(START.x, END.x), min(START.y, END.y), abs(START.x - END.x), abs(START.y - END.y));
            }
        };

        all.setPreferredSize(new Dimension(RobotHandler.X_MAX, RobotHandler.Y_MAX));
        MouseAdapter l = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                START.x = point.x;
                START.y = point.y;
                END.x = point.x;
                END.y = point.y;
                all.repaint();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                FRAME.setVisible(false);
                consumer.accept(START.x != END.x && START.y != END.y ? image.getSubimage(min(START.x, END.x), min(START.y, END.y), abs(START.x - END.x), abs(START.y - END.y)) : null);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = e.getPoint();
                END.x = point.x;
                END.y = point.y;
                all.repaint();
            }
        };
        FRAME.addMouseMotionListener(l);
        FRAME.addMouseListener(l);
        FRAME.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    FRAME.setVisible(false);
                    consumer.accept(null);
                    e.consume();
                }
            }
        });
        FRAME.add(all);
    }


    public synchronized static boolean pre(Consumer<BufferedImage> consumer) {
        if (consumer == null || FRAME.isVisible()) {
            return false;
        }
        ScreenshotAction.consumer = consumer;
        START.x = START.y = END.x = END.y = 0;
        image = RobotHandler.ROBOT.createScreenCapture(new Rectangle(0, 0, RobotHandler.X_MAX, RobotHandler.Y_MAX));
        filter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(image, null);
        FRAME.setVisible(true);
        return true;
    }
}
