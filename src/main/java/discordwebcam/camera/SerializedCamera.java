package discordwebcam.camera;

import java.io.Serializable;

/**
 * Model of a camera
 */
public class SerializedCamera implements Serializable {

	static final long serialVersionUID = -7704013905756195820L;

	public String name;

	public CameraType type;

	public String networkAddress = "no address";
	public int ID = -1;

	public int x;
	public int y;

	public double height;
	public double width;

	public boolean motionDetection;
	public boolean showMotionDetectionInPreview;
	public int motionDetectionThreshold;
	public boolean sendOnDiscord;
	public boolean downscaleQuality;
	public boolean downscalePreviewQuality;
	public int downScaleAmount;
	public Integer interpolationType;
	public boolean repaintPreviewWhenOutOfFocus;
	public int timeBetweenPreviewRepaint;
	public int rotateDeg;
	public int motionDetectionSensitivity;

	public SerializedCamera(String name, String networkAddress) {
		this();

		this.type = CameraType.NETWORK;
		this.name = name;
		this.networkAddress = networkAddress;
	}

	public SerializedCamera(String name, int ID) {
		this();

		this.type = CameraType.LOCAL;
		this.name = name;
		this.ID = ID;
	}

	public SerializedCamera() {
		x = 20;
		y = 20;
		height = 400;
		width = 400;

		motionDetection = true;
		showMotionDetectionInPreview = true;
		motionDetectionThreshold = 15;
		sendOnDiscord = false;
		downscaleQuality = false;
		downscalePreviewQuality = false;
		downScaleAmount = 1;
		interpolationType = 0;
		repaintPreviewWhenOutOfFocus = false;
		timeBetweenPreviewRepaint = 10;
		rotateDeg = 0;
		motionDetectionSensitivity = 15;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof SerializedCamera)) {
			return false;
		}
		SerializedCamera cc = (SerializedCamera) o;

		if (type == CameraType.LOCAL) {
			return cc.ID == (this.ID);
		} else {
			return cc.networkAddress.equals(this.networkAddress);
		}

	}

	@Override
	public String toString() {
		return "SerializedCamera{" + "name='" + name + '\'' + ", networkAddress='" + networkAddress + '\'' + '}';
	}

}