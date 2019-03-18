package test;

import java.io.Serializable;

public class NetworkCamera implements Serializable {

	public String name;
	public String networkAddress;

	public int x = 20;
	public int y = 20;

	public int height = 400;
	public int width = 400;

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