package discordwebcam.detection;

import java.io.File;
import java.util.Date;

/**
 * Contains information to a motion detection event
 * The date, image file location, and camera name.
 *
 * Used to send a notification message on Discord
 */
public class MotionDetectionEvent {

	public Date detectionDate;

	public File imageFile;
	public String cameraName;

	public MotionDetectionEvent(File imageFile, Date detectionDate, String cameraName) {
		this.imageFile = imageFile;
		this.detectionDate = detectionDate;
		this.cameraName = cameraName;
	}

}
