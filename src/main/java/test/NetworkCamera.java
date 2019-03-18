package test;

import java.io.Serializable;

public class NetworkCamera extends Camera implements Serializable {

	public String networkAddress;

	public NetworkCamera(String name, String networkAddress) {
		this.name = name;
		this.networkAddress = networkAddress;
	}

	@Override
	public String toString() {
		return "NetworkCamera{" +
				"networkAddress='" + networkAddress + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}

abstract class Camera {

	public String name;

}
