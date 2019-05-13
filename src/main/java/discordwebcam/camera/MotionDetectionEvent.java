package discordwebcam.camera;

import discordwebcam.Constants;
import discordwebcam.DateUtils;
import discordwebcam.FileUtils;
import discordwebcam.discord.DiscordBot;
import discordwebcam.properties.Properties;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * Used to send a notification message on Discord
 */
public class MotionDetectionEvent {

	private static org.slf4j.Logger log = LoggerFactory.getLogger(MotionDetectionEvent.class);

	public static final int ROWS = 3;
	public static final int COLUMNS = 2;
	private static final int MAX_AMOUNT_OF_IMAGES = ROWS * COLUMNS;

	static Timer t = new Timer();

	SerializedCamera serializedCamera;
	List<BufferedImage> imageBuffer = new ArrayList<>();

	public MotionDetectionEvent(SerializedCamera s) {
		this.serializedCamera = s;

		reschedulePeriodicCheck();
	}

	private static File buildFileName() {

		String folder = Properties.SAVE_IMAGES_FOLDER.getValue();

		FileUtils.makeSureFolderExists(folder);
		String newFilePath = folder + File.separator + DateUtils.getCurrentTimeStamp() + ".png";
		return new File(newFilePath);
	}

	/*
	 * Used by Discord
	 */
	public static String getPrettierTimeStamp() {
		return new Date().toLocaleString();
	}

	/**
	 * Starts a periodic check to see if images are waiting to be sent.
	 * <p>
	 * It is rescheduled (cancels previous) when new images are added.
	 */
	private void reschedulePeriodicCheck() {
		t.cancel();

		t = new Timer();

		t.schedule(new TimerTask() {
			@Override
			public void run() {
				if (imageBuffer.size() > 0) {
					buildMosaicAndSendToDiscord(imageBuffer);
				}

			}
		}, 0, 10000); //TODO make this changeable
	}

	public void motionDetected(BufferedImage image) {

		imageBuffer.add(image);

		System.out.println("Motion detected " + imageBuffer.size());

		reschedulePeriodicCheck();

		if (imageBuffer.size() > MAX_AMOUNT_OF_IMAGES) {
			buildMosaicAndSendToDiscord(imageBuffer);

			log.info("Sending images...");

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

			boolean success = FileUtils.saveToFile(mosaic, fileToSaveTo);

			if (success) {

				EmbedBuilder e = new EmbedBuilder().setTitle("MOTION DETECTED")
						.setDescription(getPrettierTimeStamp())
						.setAuthor(Constants.SOFTWARE_NAME, "", Constants.softwareIcon)
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

}
