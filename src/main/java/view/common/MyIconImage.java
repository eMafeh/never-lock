package view.common;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MyIconImage {
    private static final ClassLoader CLASS_LOADER = MyIconImage.class.getClassLoader();

    public static BufferedImage get(final String path) {
        try {
            return ImageIO.read(CLASS_LOADER.getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
