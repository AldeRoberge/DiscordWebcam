package discordwebcam.camera;

import java.io.Serializable;

/*
	Represents the two types of camera

	- NETWORK for internet camera (MJPEG stream)
	- LOCAL for USB camera
 */
public enum CameraType implements Serializable {

	NETWORK,
	LOCAL

}