package test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class Constants {

	public static ImageIcon softwareIcon;
	public static ImageIcon cameraIcon;

	static {
		try {
			cameraIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("camera.png")));
			softwareIcon = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("softwareIcon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
