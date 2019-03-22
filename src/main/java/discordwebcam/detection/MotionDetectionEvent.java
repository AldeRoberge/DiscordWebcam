package discordwebcam.detection;

import discordwebcam.Constants;
import discordwebcam.camera.SerializedCamera;
import discordwebcam.discord.DiscordBot;
import discordwebcam.properties.Properties;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * Contains information to a motion detection event The date, image file
 * location, and serializedCamera name.
 * <p>
 * Used to send a notification message on DiscordBot
 */
public class MotionDetectionEvent {

	private static org.slf4j.Logger log = LoggerFactory.getLogger(MotionDetectionEvent.class);

	static Timer t = new Timer();

	public static final int ROWS = 3;
	public static final int COLUMNS = 2;

	private static final int MAX_AMOUNT_OF_IMAGES = ROWS * COLUMNS;

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
				}

			}
		}, 0, 5000);
	}

	public void motionDetected(BufferedImage image) {

		imageBuffer.add(image);

		System.out.println("Motion detected " + imageBuffer.size());

		if (imageBuffer.size() > MAX_AMOUNT_OF_IMAGES) {
			buildMosaicAndSendToDiscord(imageBuffer);
		}

	}

	private void buildMosaicAndSendToDiscord(List<BufferedImage> bufferedImages) {

		int totalWidth = 0;
		int highestHeight = 0;
		int totalHeight = 0;

		for (int i = 0; i < bufferedImages.size(); i++) {

			BufferedImage b = bufferedImages.get(i);

			if (i < ROWS) {
				totalWidth += b.getWidth();
			}

			if (b.getHeight() > highestHeight) {
				highestHeight = b.getHeight();
			}

		}

		totalHeight = COLUMNS * highestHeight;

		BufferedImage mosaic = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);

		int currentX = 0;
		int currentY = 0;

		Graphics2D g = mosaic.createGraphics();

		for (int column = 0; column < COLUMNS; column++) {

			for (int row = 0; row < ROWS; row++) {

				int index = (column + 1) + row;

				if (bufferedImages.size() > index) {

					BufferedImage b = bufferedImages.get(index);

					g.drawImage(b, currentX, currentY, null);

					// DEBUG STRING

					String debugString = "Motion detected by " + serializedCamera.name + " at "
							+ new Date().toLocaleString() + ".";

					g.setColor(Color.BLACK);
					g.drawString(debugString, 10, 10);

					g.setColor(Color.WHITE);
					g.drawString(debugString, 10, 12);

					// END DEBUG STRING

					currentX += b.getWidth();
				}

			}

			currentX = 0;
			currentY += highestHeight;

		}

		imageBuffer.clear();

		try {

			File fileToSaveTo = buildFileName();

			boolean success = saveToFile(mosaic, fileToSaveTo);

			if (success) {

				EmbedBuilder e = new EmbedBuilder().setTitle("Camera " + serializedCamera.name + " detected motion.")
						.setDescription(getPrettierTimeStamp()).setAuthor(Constants.SOFTWARE_NAME)
						// .addField("A field", "Some text inside the field")
						// .addInlineField("An inline field", "More text")
						// .addInlineField("Another inline field", "Even more text")
						.setColor(Color.RED)
						// .setFooter("Footer", "https://cdn.discordapp.com/embed/avatars/1.png")
						.setImage(fileToSaveTo);

				DiscordBot.sendMessage(e);

			} else {
				log.error("Something went wrong with saving the detection image file '" + fileToSaveTo
						+ "'. Make sure folder '" + Properties.SAVE_IMAGES_FOLDER.getValue()
						+ "' exists and there is enough empty storage space to save the image. Defaulting to location : "
						+ fileToSaveTo.getAbsolutePath());

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Used by save to file
	 */
	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-SSS");// dd/MM/yyyy
		Date now = new Date();
		return sdfDate.format(now);
	}

	/*
	 * Used by Discord
	 */
	public static String getPrettierTimeStamp() {
		return new Date().toLocaleString();
	}

	private File buildFileName() {

		makeSureFolderExists();

		String newFilePath = Properties.SAVE_IMAGES_FOLDER.getValue() + File.separator + getCurrentTimeStamp() + ".png";

		return new File(newFilePath);

	}

	private void makeSureFolderExists() {
		if (!(new File(Properties.SAVE_IMAGES_FOLDER.getValue()).exists())) {
			log.info("Attempting to create folder '" + Properties.SAVE_IMAGES_FOLDER.getValue() + "'. Result : '"
					+ new File(Properties.SAVE_IMAGES_FOLDER.getValue()).mkdir() + "'.");
		}

	}

	private boolean saveToFile(BufferedImage image, File outputfile) throws IOException {
		return ImageIO.write(image, "png", outputfile);
	}

}
