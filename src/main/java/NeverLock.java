import common.RobotHandler;

import java.awt.*;
import java.util.Random;

public class NeverLock implements RobotHandler {
    private volatile static int count = 0;
    private volatile static Point last = new Point();
    private static final Random RANDOM = new Random();

    public static void unlock() throws InterruptedException {
        Point location = MouseInfo.getPointerInfo()
                .getLocation();
        boolean noMove = last.x == location.x && location.y == last.y;
        if (noMove) {
            if (count > 10) {
                int x = location.x + (RANDOM.nextInt(100) - 50);
                if (x < 0) {
                    x += X_MAX;

                }
                if (x > X_MAX) {
                    x -= X_MAX;
                }
                int y = location.y + (RANDOM.nextInt(100) - 50);
                if (y < 0) {
                    y += Y_MAX;

                }
                if (y > Y_MAX) {
                    y -= Y_MAX;
                }
                last.x=x;
                last.y=y;
                ROBOT.mouseMove(x,y);
                Thread.sleep(RANDOM.nextInt(5000));
                return;
            }else {
                count=0;
                last.x=location.x;
                last.y=location.y;
            }
            Thread.sleep(1000);
        }
    }
}
