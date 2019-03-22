package discordwebcam;

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

	public static BufferedImage informationIcon;
	public static BufferedImage errorIcon;

	public static BufferedImage running;
	public static BufferedImage waiting;
	public static BufferedImage stopped;

	public static BufferedImage cameraUnavailable;

	static {
		try {

			softwareIcon = read("softwareIcon.png", 64);

			gearIcon = read("gear.png", 16);
			gearIconGrayScale = read("gear_bw.png", 16);

			cameraIcon = read("camera.png", 16);
			cameraIconGrayScale = read("camera_bw.png", 16);

			loggerIcon = read("logger.png", 16);

			informationIcon = read("information.png", 16);
			errorIcon = read("error.png", 16);

			running = read("status_running.png", 16);
			waiting = read("status_waiting.png", 16);
			stopped = read("status_stopped.png", 16);

			cameraUnavailable = read("cameraUnavailable.png");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static BufferedImage read(String path, int size) throws IOException {
		return resize(read(path), size, size);
	}

	public static BufferedImage read(String path) throws IOException {
		return ImageIO.read(ClassLoader.getSystemResource(path));
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
