package discordwebcam.detection;

import java.io.File;
import java.util.Date;

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
