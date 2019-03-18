package test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Constants {

	public static final int MAX_THRESHOLD = 255;
	public static BufferedImage softwareIcon;
	public static BufferedImage gearIcon;
	public static BufferedImage cameraIcon;

	static {
		try {
			cameraIcon = ImageIO.read(ClassLoader.getSystemResource("camera.png"));
			gearIcon = ImageIO.read(ClassLoader.getSystemResource("gear.png"));
			softwareIcon = ImageIO.read(ClassLoader.getSystemResource("softwareIcon.png"));
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
