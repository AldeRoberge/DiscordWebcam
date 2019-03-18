package test;

import java.io.Serializable;

public class NetworkCamera implements Serializable {

	public String name;
	public String networkAddress;

	public int x;
	public int y;

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