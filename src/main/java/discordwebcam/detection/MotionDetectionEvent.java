package discordwebcam.detection;

import discordwebcam.camera.SerializedCamera;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * Contains information to a motion detection event
 * The date, image file location, and serializedCamera name.
 * <p>
 * Used to send a notification message on DiscordBot
 */
public class MotionDetectionEvent {

	private static org.slf4j.Logger log = LoggerFactory.getLogger(MotionDetectionEvent.class);

	private static final int MAX_AMOUNT_OF_IMAGES = 5;
	static Timer t = new Timer();

	SerializedCamera serializedCamera;

	public MotionDetectionEvent(SerializedCamera s) {
		this.serializedCamera = s;

		reschedulePeriodicCheck();
	}

	List<BufferedImage> imageBuffer = new ArrayList<>();

	private void reschedulePeriodicCheck() {
		t.cancel();

		t = new Timer();

		t.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Found " + imageBuffer.size() + " imageBuffer motion detection cameras...");

				if (imageBuffer.size() > 0) {
					buildMosaicAndSendToDiscord(imageBuffer);
					imageBuffer.clear();
				}

			}
		}, 0, 5000);
	}

	public void motionDetected(BufferedImage image) {

		imageBuffer.add(image);

		System.out.println("Motion detected " + imageBuffer.size());

		if (imageBuffer.size() > MAX_AMOUNT_OF_IMAGES) {
			buildMosaicAndSendToDiscord(imageBuffer);
			imageBuffer.clear();
		}

	}

	private void buildMosaicAndSendToDiscord(List<BufferedImage> bufferedImages) {

		int totalWidth = 0;
		int highestHeight = 0;

		for (BufferedImage b : bufferedImages) {
			totalWidth += b.getWidth();

			if (b.getHeight() > highestHeight) {
				highestHeight = b.getHeight();
			}

		}

		BufferedImage mosaic = new BufferedImage(totalWidth, highestHeight, BufferedImage.TYPE_INT_RGB);

		int currentX = 0;

		for (BufferedImage b : bufferedImages) {

			Graphics2D g = mosaic.createGraphics();

			g.drawImage(b, currentX, 0, null);

			// DEBUG STRING

			String debugString = "Motion detected by " + serializedCamera.name + " at " + new Date().toLocaleString() + ".";

			g.setColor(Color.BLACK);
			g.drawString(debugString, 10, 10);

			g.setColor(Color.WHITE);
			g.drawString(debugString, 10, 12);

			// END DEBUG STRING

			currentX += b.getWidth();
		}

		try {

			File fileToSaveTo = buildFileName();

			boolean success = saveToFile(mosaic, fileToSaveTo);

			if (success) {

			} else {
				log.error("Fatal error : Could not save detection image '" + fileToSaveTo.getAbsolutePath() + "'.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private File buildFileName() {
		return new File("./test/" + System.currentTimeMillis() + ".png");
	}

	private boolean saveToFile(BufferedImage image, File outputfile) throws IOException {
		return ImageIO.write(image, "png", outputfile);
	}

}



