package view.friend;

import common.util.ExceptionUtil;
import common.util.FileIconUtil;
import view.common.MyLiLayOut;
import view.common.MyTransferHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static common.RobotHandler.X_MAX;
import static common.RobotHandler.Y_MAX;
import static javax.swing.SwingConstants.CENTER;
import static view.common.ViewConstance.*;

/**
 * @author 88382571
 * 2019/5/15
 */
public class HistoryArea extends JPanel {
    private final MyTransferHandler newHandler;

    HistoryArea(UserView parent, MyTransferHandler newHandler) {
        setLayout(new MyLiLayOut(this, parent, new Dimension(RIGHT_WIDTH, HISTORY_HEIGHT)));
        //无边框
        setBorder(BORDER);
        if (!parent.user.windows) {
            //字号
            setFont(new Font(Font.DIALOG, Font.ITALIC, 13));
            //字色
            setForeground(WHITE);
            setBackground(Color.BLACK);
        } else {
            //字号
            setFont(new Font(Font.DIALOG, Font.ITALIC, 20));
            //字色
            setForeground(Color.BLACK);
            setBackground(WHITE);
        }
        this.newHandler = newHandler;
        setTransferHandler(newHandler);
    }

    public void addMsg(String msg, boolean self) {
        EventQueue.invokeLater(() -> {
            JTextArea area = ui(new JTextArea(msg));
            //自动换行
            area.setLineWrap(true);
            //不切断词
            area.setWrapStyleWord(true);
            //不可编辑
            area.setEditable(false);
            area.setSize(new Dimension(RIGHT_WIDTH, Integer.MAX_VALUE));
            area.setTransferHandler(newHandler);
            if (self) {
                area.setBackground(Color.WHITE);
                area.setForeground(Color.BLACK);
            }
            add(area);
        });
    }


    public void addPic(File file) {
        Image image = null;
        try {
            image = ImageIO.read(new FileInputStream(file));
        } catch (IOException e) {
          //
        }
        boolean isImage = image != null;
        if (image == null) {
            image = FileIconUtil.iconBig(file);
        }

        ImageIcon icon = new ImageIcon(image);
        JLabel comp = ui(new JLabel(isImage ? null : file.getName(), icon, CENTER));
        comp.addMouseListener(new MouseAdapter() {
            JFrame jFrame;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                if (!isImage) {
                    try {
                        Desktop.getDesktop()
                                .open(file);
                    } catch (IOException e1) {
                        ExceptionUtil.throwT(e1);
                    }
                    return;
                }
                if (jFrame == null) {
                    jFrame = new JFrame();
                    jFrame.setAlwaysOnTop(true);
                    jFrame.setResizable(false);
                    jFrame.setPreferredSize(new Dimension(icon.getIconWidth() + 6, icon.getIconHeight() + 28));
                    Dimension size = jFrame.getPreferredSize();
                    jFrame.setBounds(Math.max((X_MAX - size.width) / 2, 0), Math.max((Y_MAX - size.height) / 2, 0), size.width, size.height);
                    jFrame.add(new JComponent() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            icon.paintIcon(this, g, 0, 0);
                        }
                    });
                }
                jFrame.setVisible(true);
            }
        });
        EventQueue.invokeLater(() -> {
            add(comp);
            repaint();
        });
    }

    private <T extends JComponent> T ui(T m) {
        //无边框
        m.setBorder(getBorder());
        //字号
        m.setFont(getFont());
        //字色
        m.setForeground(getForeground());
        m.setBackground(getBackground());
        return m;
    }
}
