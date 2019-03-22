package discordwebcam.opencv;

import discordwebcam.Constants;
import discordwebcam.camera.CameraType;
import discordwebcam.camera.SerializedCamera;
import discordwebcam.detection.MotionDetectionEvent;
import discordwebcam.discord.Discord;
import discordwebcam.logger.StaticDialog;
import discordwebcam.properties.Properties;
import discordwebcam.ui.EditCameraUI;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CameraPanel extends JInternalFrame {

	static Logger log = LoggerFactory.getLogger(CameraPanel.class);

	private SerializedCamera serializedCamera;

	private boolean running = false;
	private boolean firstFrame = true;
	private VideoCapture video = null;
	private MatOfByte matOfByte = new MatOfByte();
	private Mat frameaux = new Mat();
	private Mat frame = new Mat(240, 320, CvType.CV_8UC3);
	private Mat lastFrame = new Mat(240, 320, CvType.CV_8UC3);
	private Mat currentFrame = new Mat(240, 320, CvType.CV_8UC3);
	private Mat processedFrame = new Mat(240, 320, CvType.CV_8UC3);
	private ImagePanel image;
	private int savedelay = 0;

	private CaptureThread thread;

	private JMenuItem sendOnDiscord;
	private JMenuItem motionDetection;

	public CameraPanel(SerializedCamera n, Runnable onRemove) {

		super(n.name, true, true, true, true);

		this.serializedCamera = n;

		setBackground(Constants.CAMERA_PANEL_BACKGROUND_COLOR);

		setResizable(true);
		setLayout(new BorderLayout());

		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				onRemove.run();
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				super.componentMoved(e);

				n.x = getX();
				n.y = getY();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);

				n.height = getHeight();
				n.width = getWidth();
			}
		});

		setFrameIcon(new ImageIcon(Constants.cameraIcon));

		image = new ImagePanel(Constants.cameraUnavailable);
		setLayout(new BorderLayout());
		add(image, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setVisible(true);

		JMenuItem settings = new JMenuItem("Settings");
		settings.setIcon(new ImageIcon(Constants.gearIcon));
		menuBar.add(settings);

		settings.addActionListener(e -> {
			log.info("Opening settings");
			new EditCameraUI(n, () -> {
				setTitle(n.name);
				updateMotionDetectionIcon(n.motionDetection);
				updateSendOnDiscordIcon(n.sendOnDiscord);

			}, () -> setSizeChanged());
		});

		// BEGIN MOTION DETECTION TOGGLE //

		motionDetection = new JMenuItem("Detection");
		motionDetection.addActionListener(e -> {
			n.motionDetection = !n.motionDetection;
			updateMotionDetectionIcon(n.motionDetection);
		});

		updateMotionDetectionIcon(n.motionDetection);

		menuBar.add(motionDetection);

		// BEGIN DISCORD //

		sendOnDiscord = new JMenuItem("Discord");
		sendOnDiscord.addActionListener(e -> {
			n.sendOnDiscord = !n.sendOnDiscord;
			updateSendOnDiscordIcon(n.sendOnDiscord);
		});

		updateSendOnDiscordIcon(n.sendOnDiscord);

		menuBar.add(sendOnDiscord);

		//

		add(menuBar, BorderLayout.SOUTH);

		start();
	}

	private void updateSendOnDiscordIcon(boolean send) {
		if (send) {
			sendOnDiscord.setIcon(new ImageIcon(Constants.runningIcon));
		} else {
			sendOnDiscord.setIcon(new ImageIcon(Constants.stoppedIcon));
		}
	}

	private void updateMotionDetectionIcon(boolean detection) {
		if (detection) {
			motionDetection.setIcon(new ImageIcon(Constants.runningIcon));
		} else {
			motionDetection.setIcon(new ImageIcon(Constants.stoppedIcon));
		}
	}

	private double cameraWidth = 0;
	private double cameraHeight = 0;

	private void start() {

		if (!running) {

			if (serializedCamera.type == CameraType.LOCAL) {
				video = new VideoCapture(serializedCamera.ID);
			} else if (serializedCamera.type == CameraType.NETWORK) {
				video = new VideoCapture(serializedCamera.networkAddress);
			}

			cameraWidth = video.get(Videoio.CV_CAP_PROP_FRAME_WIDTH); //get the width of frames of the video
			cameraHeight = video.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT); //get the height of frames of the video

			setSizeChanged();

			setVisible(true);

			if (video.isOpened()) {
				log.info("Is opened");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				thread = new CaptureThread();
				thread.start();
				running = true;
				firstFrame = true;
			} else {
				log.error("Could not start camera " + serializedCamera);
			}

		} else {
			log.error("Camera '" + serializedCamera.name + "' is already running!");
		}
	}

	private void setSizeChanged() {
		double actualWidth = cameraWidth;
		double actualHeight = cameraHeight;

		// Is flipped
		if (90 == serializedCamera.rotateDeg || 270 == serializedCamera.rotateDeg) {
			actualWidth = cameraHeight;
			actualHeight = cameraWidth;
		}

		serializedCamera.width = actualWidth;
		serializedCamera.height = actualHeight;

		setPreferredSize(new Dimension((int) actualWidth, (int) actualHeight));
		setSize(getPreferredSize());

	}

	/**
	 * Disposes of this object
	 */
	private void end() {

		if (running) {
			running = false;

			log.info("Shutting down camera '" + serializedCamera.name + "'...");

			try {
				Thread.sleep(500);
			} catch (Exception ex) {
			}
			video.release();
		}
	}

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-SSS");//dd/MM/yyyy
		Date now = new Date();
		return sdfDate.format(now);
	}

	public ArrayList<Rect> detection_contours(Mat frame, Mat outmat) {
		Mat v = new Mat();
		Mat vv = outmat.clone();
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = 100;
		int maxAreaIdx;
		Rect r;
		ArrayList<Rect> rect_array = new ArrayList<>();

		for (int idx = 0; idx < contours.size(); idx++) {
			Mat contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(contour);
			if (contourarea > maxArea) {
				// maxArea = contourarea;
				maxAreaIdx = idx;
				r = Imgproc.boundingRect(contours.get(maxAreaIdx));
				rect_array.add(r);
				Imgproc.drawContours(frame, contours, maxAreaIdx, new Scalar(0, 0, 255));
			}
		}

		v.release();
		return rect_array;
	}

	class CaptureThread extends Thread {

		@Override
		public void run() {
			if (video.isOpened()) {
				while (running) {

					try {

						video.read(frameaux);

						//TODO why does read work, but not retrieve?

						//video.retrieve(frameaux);
						Imgproc.resize(frameaux, frame, frame.size());
						frame.copyTo(currentFrame);



						if (firstFrame) {
							frame.copyTo(lastFrame);
							firstFrame = false;
							continue;
						}

						flip(frame, frameaux, serializedCamera.rotateDeg);

						if (serializedCamera.downscalePreviewQuality) {
							lowerQuality();
						}



						if (serializedCamera.motionDetection) {
							Imgproc.GaussianBlur(currentFrame, currentFrame, new Size(3, 3), 0);
							Imgproc.GaussianBlur(lastFrame, lastFrame, new Size(3, 3), 0);

							//bsMOG.apply(frame, processedFrame, 0.005);
							Core.subtract(currentFrame, lastFrame, processedFrame);
							//Core.absdiff(frame,lastFrame,processedFrame);

							Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_RGB2GRAY);
							//

							//Imgproc.adaptiveThreshold(processedFrame, processedFrame, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);
							Imgproc.threshold(processedFrame, processedFrame, serializedCamera.threshold, 255, Imgproc.THRESH_BINARY);

							ArrayList<Rect> array = detection_contours(currentFrame, processedFrame);
							///*
							if (array.size() > 0) {
								Iterator<Rect> it2 = array.iterator();
								while (it2.hasNext()) {
									Rect obj = it2.next();
									Imgproc.rectangle(currentFrame, obj.br(), obj.tl(),
											new Scalar(0, 255, 0), 1);
								}
							}
							//*/

							double sensibility = 15;
							//log.info(sensibility);
							double nonZeroPixels = Core.countNonZero(processedFrame);
							//log.info("nonZeroPixels: " + nonZeroPixels);

							double nrows = processedFrame.rows();
							double ncols = processedFrame.cols();
							double total = nrows * ncols / 10;

							double detections = (nonZeroPixels / total) * 100;
							//log.info(detections);
							if (detections >= sensibility) {
								//log.info("ALARM ENABLED!");
								Imgproc.putText(currentFrame, "MOTION DETECTED",
										new Point(5, currentFrame.cols() / 2), //currentFrame.rows()/2 currentFrame.cols()/2
										Core.FONT_HERSHEY_TRIPLEX, 1d, new Scalar(0, 0, 255));

								if (serializedCamera.sendOnDiscord) {
									if (savedelay == 2) {
										savedelay = 0;

										if (!(new File(Properties.SAVE_IMAGES_FOLDER.getValue()).exists())) {
											log.info("Attempting to create folder '" + Properties.SAVE_IMAGES_FOLDER.getValue() + "'. Result : '" + new File(Properties.SAVE_IMAGES_FOLDER.getValue()).mkdir() + "'.");
										}

										String newFilePath = Properties.SAVE_IMAGES_FOLDER.getValue()
												+ File.separator + getCurrentTimeStamp() + ".png";

										if (!serializedCamera.downscalePreviewQuality) { // Has not already been downscaled
											lowerQuality();
										}

										Imgcodecs.imencode(".jpg", frame, matOfByte);
										byte[] byteArray = matOfByte.toArray();

										BufferedImage buf;
										InputStream in;

										try {
											in = new ByteArrayInputStream(byteArray);
											buf = ImageIO.read(in);

											File outputfile = new File(newFilePath);
											ImageIO.write(buf, "png", outputfile);

											MotionDetectionEvent e = new MotionDetectionEvent(outputfile, new Date(), serializedCamera.name);

											Discord.notifyDetection(e);

										} catch (Exception ex) {

											log.error("Something went wrong with saving the detection image file '" + newFilePath + "'. Make sure folder '" + Properties.SAVE_IMAGES_FOLDER.getValue() + "' exists and there is enough empty storage space to save the image.");

											ex.printStackTrace();
										}

									} else
										savedelay = savedelay + 1;
								}
							} else {
								savedelay = 0;
							}

							//currentFrame.copyTo(processedFrame);

						} else {

							//frame.copyTo(processedFrame);

						}

						currentFrame.copyTo(processedFrame);

						Imgcodecs.imencode(".jpg", frameaux, matOfByte);
						byte[] byteArray = matOfByte.toArray();

						InputStream in;

						BufferedImage bufImage = null;

						try {
							in = new ByteArrayInputStream(byteArray);
							bufImage = ImageIO.read(in);

							//image.updateImage(new ImageIcon("figs/lena.png").getImage());
							image.updateImage(bufImage);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						frame.copyTo(lastFrame);

						try {
							Thread.sleep(1);
						} catch (Exception ex) {
						}

					} catch (Exception e) {
						log.error("Error : ", e);

						StaticDialog.display("Error with camera " + serializedCamera.name, "Error with camera. Make sure there is no process already using it.", e);

						running = false;

					}
				}
			}
		}

		private void lowerQuality() {

			double percentage = (double) serializedCamera.downScaleAmount / 100;

			System.out.println("Percentage : " + percentage);

			if (serializedCamera.downScaleAmount != 0) {
				System.out.println("Old quality : " + frameaux.size());

				int newWidth = (int) (serializedCamera.width * percentage);
				int newHeight = (int) (serializedCamera.height * percentage);

				System.out.println("newWidth : " + newWidth + ", " + newHeight);

				Imgproc.resize(frame, frameaux, new Size(newWidth, newHeight), 0, 0, serializedCamera.interpolationType);

				System.out.println("New quality : " + frameaux.size());
			}

		}
	}

	private void flip(Mat src, Mat dst, int deg) {

		log.info("Flipping deg " + deg);

		if (deg == 270 || deg == -90) {
			// Rotate clockwise 270 degrees
			Core.transpose(src, dst);
			Core.flip(dst, dst, 0);
		} else if (deg == 180 || deg == -180) {
			// Rotate clockwise 180 degrees
			Core.flip(src, dst, -1);
		} else if (deg == 90 || deg == -270) {
			// Rotate clockwise 90 degrees
			Core.transpose(src, dst);
			Core.flip(dst, dst, 1);
		}
	}

}

class ImagePanel extends JPanel {
	private Image img;

	public ImagePanel(Image img) {
		this.img = img;
	}

	public void updateImage(Image img) {
		this.img = img;
		validate();
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

		String debugInfo = "Image height : " + img.getWidth(this) + ", width : " + img.getHeight(this);

		g.setColor(Color.BLACK);
		g.drawString(debugInfo, 10, 10);
		g.setColor(Color.WHITE);
		g.drawString(debugInfo, 10, 11);
	}
}
