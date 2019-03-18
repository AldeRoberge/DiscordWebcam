package test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Constants {

	public static final int MAX_THRESHOLD = 255;
	public static final String SOFTWARE_NAME = "Final Security Suite";

	public static final Color CAMERA_PANEL_BACKGROUND_COLOR = Color.BLACK;

	public static BufferedImage softwareIcon;

	public static BufferedImage gearIcon;
	public static BufferedImage gearIconGrayScale;

	public static BufferedImage cameraIcon;
	public static BufferedImage cameraIconGrayScale;


	public static BufferedImage loggerIcon;

	static {
		try {

			softwareIcon = ImageIO.read(ClassLoader.getSystemResource("softwareIcon.png"));

			gearIcon = ImageIO.read(ClassLoader.getSystemResource("gear.png"));
			gearIconGrayScale = ImageIO.read(ClassLoader.getSystemResource("gear_bw.png"));

			cameraIcon = ImageIO.read(ClassLoader.getSystemResource("camera.png"));
			cameraIconGrayScale = ImageIO.read(ClassLoader.getSystemResource("camera_bw.png"));

			loggerIcon = ImageIO.read(ClassLoader.getSystemResource("logger.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			cameraIcon = resize(cameraIcon, 64, 64);
			gearIcon = resize(gearIcon, 16, 16);
			softwareIcon = resize(softwareIcon, 64, 64);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

}
