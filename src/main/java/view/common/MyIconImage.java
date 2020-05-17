package view.common;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author 88382571
 * 2019/5/14
 */
public class MyIconImage {
	private static final ClassLoader CLASS_LOADER = MyIconImage.class.getClassLoader();
	
	public static BufferedImage get(String path) {
		try {
			return ImageIO.read(CLASS_LOADER.getResourceAsStream(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
