package discordwebcam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Constants {

	static Logger log = LoggerFactory.getLogger(Constants.class);

	public static final String SOFTWARE_NAME = "Final Security Suite";

	public static final int MAX_THRESHOLD = 25;
	public static final int MAX_SENSITIVITY = 1000;

	public static final Color CAMERA_PANEL_BACKGROUND_COLOR = Color.BLACK;

	public static BufferedImage softwareIcon;

	public static BufferedImage gearIcon;
	public static BufferedImage gearIconGrayScale;

	public static BufferedImage cameraIcon;
	public static BufferedImage cameraIconGrayScale;

	public static BufferedImage loggerIcon;

	public static BufferedImage informationIcon;
	public static BufferedImage errorIcon;

	public static BufferedImage runningIcon;
	public static BufferedImage waitingIcon;
	public static BufferedImage stoppedIcon;

	public static BufferedImage arrowRotate1Icon;
	public static BufferedImage arrowRotate2Icon;
	public static BufferedImage arrowRotate3Icon;
	public static BufferedImage arrowRotate4Icon;

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

			arrowRotate1Icon = read("arrow_rotate1.png", 16);
			arrowRotate2Icon = read("arrow_rotate2.png", 16);
			arrowRotate3Icon = read("arrow_rotate3.png", 16);
			arrowRotate4Icon = read("arrow_rotate4.png", 16);

			runningIcon = read("status_running.png", 16);
			waitingIcon = read("status_waiting.png", 16);
			stoppedIcon = read("status_stopped.png", 16);

			cameraUnavailable = read("cameraUnavailable.png");

		} catch (IOException e) {
			e.printStackTrace();

			log.error("Error while loading icons.");
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
