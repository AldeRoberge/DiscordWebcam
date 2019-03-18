package test;

import java.io.Serializable;

public class NetworkCamera implements Serializable {

	static final long serialVersionUID = -7704013905756195820L;

	public String name;
	public String networkAddress;

	public int x = 20;
	public int y = 20;

	public int height = 400;
	public int width = 400;

	public boolean motionDetection = false;
	public int threshold = 15; //max = 255

	public boolean sendOnDiscord;

	public NetworkCamera(String name, String networkAddress) {
		this.name = name;
		this.networkAddress = networkAddress;
	}

	@Override
	public String toString() {
		return "NetworkCamera{" +
				"name='" + name + '\'' +
				", networkAddress='" + networkAddress + '\'' +
				", x=" + x +
				", y=" + y +
				", height=" + height +
				", width=" + width +
				", motionDetection=" + motionDetection +
				", threshold=" + threshold +
				", sendOnDiscord=" + sendOnDiscord +
				'}';
	}

}