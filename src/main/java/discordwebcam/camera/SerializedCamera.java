package discordwebcam.camera;

import com.sun.istack.internal.NotNull;
import org.opencv.core.Size;

import java.io.Serializable;

public class SerializedCamera implements Serializable {

	static final long serialVersionUID = -7704013905756195820L;

	public String name;

	public CameraType type;

	public String networkAddress;
	public int ID;

	public int x = 20;
	public int y = 20;

	public double height = 400;
	public double width = 400;

	public boolean motionDetection = false;
	public int threshold = 15; //max = 255

	public boolean sendOnDiscord;

	public boolean downscaleQuality;
	public int downScaleAmount = 1;
	
	public boolean downscalePreviewQuality = false;
	public Integer interpolationType = 0;

	public int rotateDeg;

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
		return "SerializedCamera{" +
				"name='" + name + '\'' +
				", networkAddress='" + networkAddress + '\'' +
				'}';
	}

}

