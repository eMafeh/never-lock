package view.common;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;

public interface ViewConstance {
    static int multiply618(int n) {
        return (int) (n * 0.618);
    }

    int WIDTH = 648;
    int HEIGHT = multiply618(WIDTH);
    int BUTTON_WIDTH = 33;
    int BUTTON_HEIGHT = multiply618(BUTTON_WIDTH);
    int LINE_HEIGHT = BUTTON_HEIGHT;
    int MAIN_HEIGHT = HEIGHT - LINE_HEIGHT;
    int USER_HEIGHT = MAIN_HEIGHT / 8;
    int USER_WIDTH = multiply618(USER_HEIGHT) + 2 * USER_HEIGHT;
    int RIGHT_WIDTH = WIDTH - USER_WIDTH;
    int HISTORY_HEIGHT = multiply618(MAIN_HEIGHT);
    int INPUT_HEIGHT = MAIN_HEIGHT - HISTORY_HEIGHT;
    Color WHITE = new Color(0xf5, 0xf5, 0xf5);
    BufferedImage ONLINE = MyIconImage.get("icon.png");
    BufferedImage OFFLINE = MyIconImage.get("silence.png");
    Color LEAVE = new Color(0xe6, 0xe5, 0xe5);
    Color HOVER = new Color(0xdd, 0xdb, 0xda);
    Color CHOSE = new Color(0xc6, 0xc5, 0xc4);
    Border BORDER = BorderFactory.createEmptyBorder();
}