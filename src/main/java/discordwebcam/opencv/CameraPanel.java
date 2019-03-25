package discordwebcam.opencv;

import discordwebcam.Constants;
import discordwebcam.camera.CameraType;
import discordwebcam.camera.SerializedCamera;
import discordwebcam.detection.MotionDetectionEvent;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CameraPanel extends JInternalFrame {

	static Logger log = LoggerFactory.getLogger(CameraPanel.class);
	ArrayList<Rect> detectionsSquares = new ArrayList<>();
	MotionDetectionEvent motionDetectionEvent;
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
	private JProgressBar progressBar = new JProgressBar(0, 100);
	private double cameraWidth = 0;
	private double cameraHeight = 0;

	public CameraPanel(SerializedCamera n, Runnable onRemove) {

		super(n.name, true, true, true, true);

		this.serializedCamera = n;

		motionDetectionEvent = new MotionDetectionEvent(serializedCamera);

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

		progressBar.setSize(new Dimension(getWidth(), 100));
		progressBar.setForeground(Color.RED);
		progressBar.setBackground(Color.WHITE);
		progressBar.setStringPainted(true);
		menuBar.add(progressBar);

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
			progressBar.setString(null);
			progressBar.setValue(0);
		} else {
			motionDetection.setIcon(new ImageIcon(Constants.stoppedIcon));
			progressBar.setString("Deactivated");
			progressBar.setValue(progressBar.getMaximum());
		}
	}

	private void start() {

		if (!running) {

			if (serializedCamera.type == CameraType.LOCAL) {
				video = new VideoCapture(serializedCamera.ID);
			} else if (serializedCamera.type == CameraType.NETWORK) {
				video = new VideoCapture(serializedCamera.networkAddress);
			}

			cameraWidth = video.get(Videoio.CV_CAP_PROP_FRAME_WIDTH); // get the width of frames of the video
			cameraHeight = video.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT); // get the height of frames of the video

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

	private void flip(Mat src, Mat dst, int deg) {

		if (deg != 0) {

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

	class CaptureThread extends Thread {

		private long lastUpdate = System.currentTimeMillis();

		@Override
		public void run() {
			if (video.isOpened()) {
				while (running) {

					try {

						video.read(frameaux);

						// TODO why does read work, but not retrieve?

						// video.retrieve(frameaux);
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

							// bsMOG.apply(frame, processedFrame, 0.005);
							Core.subtract(currentFrame, lastFrame, processedFrame);
							// Core.absdiff(frame,lastFrame,processedFrame);

							Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_RGB2GRAY);
							//

							// Imgproc.adaptiveThreshold(processedFrame, processedFrame, 255,
							// Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);
							Imgproc.threshold(processedFrame, processedFrame, serializedCamera.motionDetectionThreshold,
									255, Imgproc.THRESH_BINARY);

							detectionsSquares = detection_contours(currentFrame, processedFrame);
							if (detectionsSquares.size() > 0) {
								Iterator<Rect> it2 = detectionsSquares.iterator();
								while (it2.hasNext()) {
									Rect obj = it2.next();
									Imgproc.rectangle(currentFrame, obj.br(), obj.tl(), new Scalar(0, 255, 0), 1);
								}
							}

							double sensibility = serializedCamera.motionDetectionSensitivity;
							// log.info(sensibility);
							double nonZeroPixels = Core.countNonZero(processedFrame);
							// log.info("nonZeroPixels: " + nonZeroPixels);

							double nrows = processedFrame.rows();
							double ncols = processedFrame.cols();
							double total = nrows * ncols / 10;

							int detections = (int) ((nonZeroPixels / total) * 100);
							// log.info(detections);

							progressBar.setValue(detections);

							if (detections >= sensibility) {

								if (serializedCamera.sendOnDiscord) {

									if (savedelay == 2) {
										savedelay = 0;

										System.out.println("Motions : " + detections + ", " + sensibility);
										takeScreenshotAndSendToDiscord();
									} else {
										savedelay = savedelay + 1;
									}

								}
							} else {
								savedelay = 0;
							}

							// currentFrame.copyTo(processedFrame);

						} else {

							// frame.copyTo(processedFrame);

						}

						if (System.currentTimeMillis() - lastUpdate > serializedCamera.timeBetweenPreviewRepaint) {
							lastUpdate = System.currentTimeMillis();

							currentFrame.copyTo(processedFrame);

							if (serializedCamera.showMotionDetectionInPreview) {

								for (Rect obj : detectionsSquares) {
									//Imgproc.circle(frameaux, new Point(obj.x * frameaux.width() / processedFrame.width(), obj.y * frameaux.height() / processedFrame.height()), 2, new Scalar(0, 255, 0));

									Imgproc.rectangle(frameaux, new Point(obj.x * frameaux.width() / processedFrame.width(), obj.y * frameaux.height() / processedFrame.height()), new Point(obj.x + obj.width * frameaux.width() / processedFrame.width(), obj.y + obj.height * frameaux.height() / processedFrame.height()), new Scalar(0, 255, 0));

								}

							}

							Imgcodecs.imencode(".jpg", frameaux, matOfByte);

							try {

								InputStream in = new ByteArrayInputStream(matOfByte.toArray());
								BufferedImage bufImage = ImageIO.read(in);

								// image.updateImage(new ImageIcon("figs/lena.png").getImage());
								image.updateImage(bufImage);
							} catch (Exception ex) {
								ex.printStackTrace();
							}

							frame.copyTo(lastFrame);
						}

						try {
							Thread.sleep(1);
						} catch (Exception ex) {
						}

					} catch (Exception e) {
						log.error("Error : ", e);

						StaticDialog.display("Error with camera " + serializedCamera.name,
								"Error with camera. Make sure there is no process already using it.", e);

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

				Imgproc.resize(frame, frameaux, new Size(newWidth, newHeight), 0, 0,
						serializedCamera.interpolationType);

				System.out.println("New quality : " + frameaux.size());
			}

		}
	}

	public void takeScreenshotAndSendToDiscord() {

		Imgcodecs.imencode(".jpg", frame, matOfByte);
		byte[] byteArray = matOfByte.toArray();

		BufferedImage buf;
		InputStream in;

		in = new ByteArrayInputStream(byteArray);
		try {
			buf = ImageIO.read(in);
			motionDetectionEvent.motionDetected(buf);
		} catch (IOException e) {
			e.printStackTrace();
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
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

		if (Properties.DEBUG_SHOW_IMAGE_HEIGHT.getBooleanValue()) {
			String debugInfo = "Image height : " + img.getWidth(this) + ", width : " + img.getHeight(this);
			g.setColor(Color.BLACK);
			g.drawString(debugInfo, 10, 10);
			g.setColor(Color.WHITE);
			g.drawString(debugInfo, 10, 11);
		}

	}
}
