package common;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CmdInstructions {
    public static void lock() throws IOException {
        Runtime.getRuntime()
                .exec("rundll32.exe user32.dll,LockWorkStation");
    }

    private static void pop(int time, String msg, String title) throws IOException {
        Runtime.getRuntime()
                .exec("mshta \"javascript:new ActiveXObject('WScript.Shell').popup('" + msg + "'," + time + ",'" + title + "',64);window.close();\"");
    }

    private static void image() throws IOException {
        BufferedImage screenCapture = RobotHandler.ROBOT.createScreenCapture(new Rectangle(0, 0, RobotHandler.X_MAX, RobotHandler.Y_MAX));
        ImageIO.write(screenCapture, "png", new File("a.png"));
    }
}
