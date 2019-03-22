package discordwebcam.camera;

import com.sun.istack.internal.NotNull;
import org.opencv.core.Size;

import java.io.Serializable;

public class SerializedCamera implements Serializable {

	static final long serialVersionUID = -7704013905756195820L;

	public String name = "Unnamed camera";

	public CameraType type = CameraType.LOCAL;

	public String networkAddress = "no address";
	public int ID = -1;

	public int x = 20;
	public int y = 20;

	public double height = 400;
	public double width = 400;

	public boolean motionDetection = false;
	public boolean showMotionDetectionInPreview = false;
	public int motionDetectionThreshold = 15;

	public boolean sendOnDiscord = false;

	public boolean downscaleQuality = false;
	public boolean downscalePreviewQuality = false;
	public int downScaleAmount = 1;

	public Integer interpolationType = 0;

	public boolean repaintPreviewWhenOutOfFocus = false;
	public int timeBetweenPreviewRepaint = 10;

	public int rotateDeg = 0;
	public double sensitivity;

	public SerializedCamera(String name, String networkAddress) {
		this.type = CameraType.NETWORK;

		this.name = name;
		this.networkAddress = networkAddress;
	}

	public SerializedCamera(String name, int ID) {
		this.type = CameraType.LOCAL;

		this.name = name;
		this.ID = ID;
	}

	@Override
	public String toString() {
		return "SerializedCamera{" + "name='" + name + '\'' + ", networkAddress='" + networkAddress + '\'' + '}';
	}

}
