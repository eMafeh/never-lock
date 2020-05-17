package common;

import java.awt.*;
import java.util.function.Supplier;

/**
 * @author 88382571
 * 2019/4/22
 */
public interface RobotHandler {
	/**
	 * 机器人
	 */
	Robot ROBOT = ((Supplier<Robot>) () -> {
		try {
			return new Robot();
		} catch (AWTException e) {
			throw new RuntimeException();
		}
	}).get();
	Point MAX = ((Supplier<Point>) () -> {
		//获取当前鼠标
		Point lastPoint = MouseInfo.getPointerInfo()
				.getLocation();
		
		//获取鼠标最大位移
		ROBOT.mouseMove(Integer.MAX_VALUE, Integer.MAX_VALUE);
		Point max = MouseInfo.getPointerInfo()
				.getLocation();
		//鼠标移回原位
		ROBOT.mouseMove(lastPoint.x, lastPoint.y);
		
		//鼠标可以点击的范围
		return max;
	}).get();
	/**
	 * 鼠标可以点击的范围
	 */
	int X_MAX = MAX.x;
	int Y_MAX = MAX.y;
}
