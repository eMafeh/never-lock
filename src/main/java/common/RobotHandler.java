package common;

import java.awt.*;
import java.util.function.Supplier;

public interface RobotHandler {
    /**
     * 机器人
     */
    Robot ROBOT = ((Supplier<Robot>) () -> {
        try {
            return new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }).get();
    Point MAX = ((Supplier<Point>) () -> {
        Point location = MouseInfo.getPointerInfo()
                .getLocation();
        ROBOT.mouseMove(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point max = MouseInfo.getPointerInfo()
                .getLocation();
        ROBOT.mouseMove(location.x, location.y);
        return max;
    }).get();
    int X_MAX = MAX.x;
    int Y_MAX = MAX.y;
}
