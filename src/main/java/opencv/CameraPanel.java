package opencv;

import discord.Discord;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.Properties;
import test.Constants;
import test.NetworkCamera;
import test.detection.MotionDetectionEvent;
import test.ui.EditCameraUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary("opencv_ffmpeg342_64"); //crucial to use IP Camera
	}

	static Logger log = LoggerFactory.getLogger(CameraPanel.class);

	private NetworkCamera networkCamera;

	private Boolean begin = false;
	private Boolean firstFrame = true;
	private VideoCapture video = null;
	private MatOfByte matOfByte = new MatOfByte();
	private Mat frameaux = new Mat();
	private Mat frame = new Mat(240, 320, CvType.CV_8UC3);
	private Mat lastFrame = new Mat(240, 320, CvType.CV_8UC3);
	private Mat currentFrame = new Mat(240, 320, CvType.CV_8UC3);
	private Mat processedFrame = new Mat(240, 320, CvType.CV_8UC3);
	private ImagePanel image;
	private int savedelay = 0;

	CaptureThread thread;

	JMenuBar menuBar;

	public static void main(String[] args) {
		new CameraPanel(new NetworkCamera("Test camera", "http://192.168.0.107:8080/video"
		), () -> log.info("On close"));
	}

	JMenuItem sendOnDiscord;
	JMenuItem motionDetection;

	public CameraPanel(NetworkCamera n, Runnable onRemove) {

		super(n.name, true, true, true, true);

		this.networkCamera = n;

		setBackground(Constants.CAMERA_PANEL_BACKGROUND_COLOR);

		setSize(n.width, n.height);
		setLocation(n.x, n.y);

		setResizable(true);
		setLayout(new BorderLayout());

		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				super.internalFrameActivated(e);
				receivedFocus();
			}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {
				super.internalFrameDeactivated(e);
				lostFocus();
			}
		});

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

		menuBar = new JMenuBar();
		menuBar.setVisible(false);

		JMenuItem settings = new JMenuItem("Settings");
		settings.setIcon(new ImageIcon(Constants.gearIcon));
		menuBar.add(settings);

		settings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Opening settings");
				new EditCameraUI(n, new Runnable() {
					@Override
					public void run() {
						setTitle(n.name);
						updateMotionDetectionIcon(n.motionDetection);
						updateSendOnDiscordIcon(n.sendOnDiscord);
					}
				});
			}
		});

		// BEGIN MOTION DETECTION TOGGLE //

		motionDetection = new JMenuItem("Detection");
		motionDetection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (n.motionDetection) {
					n.motionDetection = false;
				} else {
					n.motionDetection = true;
				}

				updateMotionDetectionIcon(n.motionDetection);
			}

		});

		updateMotionDetectionIcon(n.motionDetection);

		menuBar.add(motionDetection);

		// BEGIN DISCORD //

		sendOnDiscord = new JMenuItem("Discord");
		sendOnDiscord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (n.sendOnDiscord) {
					n.sendOnDiscord = false;
				} else {
					n.sendOnDiscord = true;
				}

				updateSendOnDiscordIcon(n.sendOnDiscord);
			}

		});

		updateSendOnDiscordIcon(n.sendOnDiscord);

		menuBar.add(sendOnDiscord);

		//

		add(menuBar, BorderLayout.SOUTH);

		setVisible(true);

		start();
	}

	private void updateSendOnDiscordIcon(boolean send) {
		if (send) {
			sendOnDiscord.setIcon(new ImageIcon(Constants.running));
		} else {
			sendOnDiscord.setIcon(new ImageIcon(Constants.stopped));
		}
	}

	private void updateMotionDetectionIcon(boolean detection) {
		if (detection) {
			motionDetection.setIcon(new ImageIcon(Constants.running));
		} else {
			motionDetection.setIcon(new ImageIcon(Constants.stopped));
		}
	}

	public void receivedFocus() {
		menuBar.setVisible(true);
	}

	public void lostFocus() {
		menuBar.setVisible(false);
	}

	private void start() {

		if (!begin) {

			video = new VideoCapture(networkCamera.networkAddress);

			double dWidth = video.get(Videoio.CV_CAP_PROP_FRAME_WIDTH); //get the width of frames of the video
			double dHeight = video.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT); //get the height of frames of the video

			setPreferredSize(new Dimension((int) dWidth, (int) dHeight));

			log.info("Got width : " + dWidth + ", height : " + dHeight);

			if (video.isOpened()) {
				log.info("Is opened");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				thread = new CaptureThread();
				thread.start();
				begin = true;
				firstFrame = true;
			}
		}
	}

	/**
	 * Disposes of this object
	 */
	private void end() {

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (begin) {
			try {
				Thread.sleep(500);
			} catch (Exception ex) {
			}
			video.release();
			begin = false;

		}
	}

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-SSS");//dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
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
				while (begin) {
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

					if (networkCamera.motionDetection) {
						Imgproc.GaussianBlur(currentFrame, currentFrame, new Size(3, 3), 0);
						Imgproc.GaussianBlur(lastFrame, lastFrame, new Size(3, 3), 0);

						//bsMOG.apply(frame, processedFrame, 0.005);
						Core.subtract(currentFrame, lastFrame, processedFrame);
						//Core.absdiff(frame,lastFrame,processedFrame);

						Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_RGB2GRAY);
						//

						//Imgproc.adaptiveThreshold(processedFrame, processedFrame, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);
						Imgproc.threshold(processedFrame, processedFrame, networkCamera.threshold, 255, Imgproc.THRESH_BINARY);

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

							if (networkCamera.sendOnDiscord) {
								if (savedelay == 2) {
									savedelay = 0;

									if (!(new File(Properties.SAVE_IMAGES_FOLDER.getValue()).exists())) {
										log.info("Attempting to create folder '" + Properties.SAVE_IMAGES_FOLDER.getValue() + "'. Result : '" + new File(Properties.SAVE_IMAGES_FOLDER.getValue()).mkdir() + "'.");
									}

									String newFilePath = Properties.SAVE_IMAGES_FOLDER.getValue()
											+ File.separator + getCurrentTimeStamp() + ".png";

									Imgcodecs.imencode(".jpg", frame, matOfByte);
									byte[] byteArray = matOfByte.toArray();

									BufferedImage buf;
									InputStream in;

									try {
										in = new ByteArrayInputStream(byteArray);
										buf = ImageIO.read(in);

										File outputfile = new File(newFilePath);
										ImageIO.write(buf, "png", outputfile);

										MotionDetectionEvent e = new MotionDetectionEvent(outputfile, new Date(), networkCamera.name);

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

					Imgcodecs.imencode(".jpg", processedFrame, matOfByte);
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

				}
			}
		}
	}

}

class ImagePanel extends JPanel {
	private Image img;

	public ImagePanel(Image img) {
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
		//setBorder(BorderFactory.createLineBorder(Color.black));
	}

	public void updateImage(Image img) {
		this.img = img;
		validate();
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
}
