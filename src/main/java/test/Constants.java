package test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Constants {

	public static BufferedImage softwareIcon;
	public static BufferedImage gearIcon;
	public static ImageIcon cameraIcon;

	static {
		try {
			cameraIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("camera.png")));
			gearIcon = ImageIO.read(ClassLoader.getSystemResource("gear.png"));
			softwareIcon = ImageIO.read(ClassLoader.getSystemResource("softwareIcon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
