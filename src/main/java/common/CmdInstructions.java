package common;

import common.util.ExceptionUtil;
import common.util.SystemUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author 88382571
 * 2019/4/22
 */
public class CmdInstructions implements RobotHandler {
    private static final String SELF = new File(System.getProperty("sun.java.command")).getName();

    public static void lock() throws IOException {
        checkWindow();
        Runtime.getRuntime()
                .exec("rundll32.exe user32.dll,LockWorkStation");
    }

    private static void checkWindow() {
        ExceptionUtil.isTrue(SystemUtil.isWindows());
    }

    private static void pop(int time, String msg, String title) throws IOException {
        checkWindow();
        Runtime.getRuntime()
                .exec("mshta \"javascript:new ActiveXObject('WScript.Shell').popup('" + msg + "'," + time + ",'" + title + "',64);window.close();\"");
    }

    private static void image() throws IOException {
        checkWindow();
        BufferedImage screenCapture = RobotHandler.ROBOT.createScreenCapture(
                new Rectangle(0, 0, RobotHandler.X_MAX, RobotHandler.Y_MAX));
        ImageIO.write(screenCapture, "png", new File("a.png"));
    }

    private static void restart(int delay) throws IOException {
        checkWindow();
        Runtime.getRuntime()
                .exec("cmd /c ping localhost -n " + delay + " >nul && java -jar " + SELF);
    }

    public static void inProp(String path, byte[] value) throws IOException {
        Files.write(Paths.get(path), value);
        Runtime.getRuntime()
                .exec("cmd /c ping localhost -n 2 >nul && jar uf " + SELF + " " + path + " && del " + path);
    }
}