package view.common;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author 88382571
 * 2019/5/7
 */
public interface ViewConstance {
    static int multiply618(int n) {
        return (int) (n * 0.618);
    }

    /**
     * 总宽度
     */
    int WIDTH = 880;
    /**
     * 总高度
     */
    int HEIGHT = multiply618(WIDTH);

    /**
     * 按钮宽度
     */
    int BUTTON_WIDTH = 33;
    /**
     * 按钮高度
     */
    int BUTTON_HEIGHT = multiply618(BUTTON_WIDTH);
    /**
     * 线高
     */
    int LINE_HEIGHT = BUTTON_HEIGHT;
    /**
     * 主界面高度
     */
    int MAIN_HEIGHT = HEIGHT - LINE_HEIGHT;
    /**
     * 用户列表图标高度
     */
    int USER_HEIGHT = MAIN_HEIGHT / 10;
    /**
     * 用户列表图标宽度
     */
    int USER_WIDTH = multiply618(USER_HEIGHT) + 2 * USER_HEIGHT;

    /**
     * 右侧宽度
     */
    int RIGHT_WIDTH = WIDTH - USER_WIDTH;

    /**
     * 右侧历史记录高度
     */
    int HISTORY_HEIGHT = multiply618(MAIN_HEIGHT);
    /**
     * 右侧输入栏高度
     */
    int INPUT_HEIGHT = MAIN_HEIGHT - HISTORY_HEIGHT - 2 * BUTTON_HEIGHT;


    Color WHITE = new Color(0xf5, 0xf5, 0xf5);

    BufferedImage ONLINE = MyIconImage.get("icon.png");
    BufferedImage OFFLINE = MyIconImage.get("silence.png");
    BufferedImage LINK = MyIconImage.get("link.png");
    BufferedImage UNLINK = MyIconImage.get("unlink.png");
    BufferedImage REMOVE = MyIconImage.get("remove.png");
    BufferedImage PEN = MyIconImage.get("pen.png");
    BufferedImage CLOSE = MyIconImage.get("close.png");
    BufferedImage HIDDEN = MyIconImage.get("hidden.png");
    Color LEAVE = new Color(0xe6, 0xe5, 0xe5);
    Color HOVER = new Color(0xdd, 0xdb, 0xda);
    Color CHOSE = new Color(0xc6, 0xc5, 0xc4);
    Border BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 0);
}
