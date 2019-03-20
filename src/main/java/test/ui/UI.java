package test.ui;

import alde.commons.util.file.FileSizeToString;
import alde.commons.util.window.UtilityJFrame;
import opencv.CameraPanel;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.Properties;
import test.CameraListSerializer;
import test.Constants;
import test.camera.SerializedCamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.TimerTask;

public class UI extends UtilityJFrame {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary("opencv_ffmpeg342_64"); //crucial to use IP Camera
	}

	static Logger log = LoggerFactory.getLogger(UI.class);

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.error("Could not set look and feel. ", e);
		}

		JFrame.setDefaultLookAndFeelDecorated(false);

		if (Properties.IS_FIRST_LAUNCH.getBooleanValue()) {
			showEditPropertiesPanel(true);
		} else {
			showUI();
		}
	}

	// This function is shared with UI's Edit -> Edit properties. Thats why we use 'runOnClose' to differentiate
	static void showEditPropertiesPanel(boolean runOnClose) {
		UtilityJFrame f = new UtilityJFrame();

		f.setIconImage(Constants.gearIcon);

		f.setTitle("Set properties");
		f.add(Properties.getPropertiesPanel(), BorderLayout.CENTER);
		Button closeButton = new Button("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Close");
				f.setVisible(false);

				if (runOnClose) {
					showUI();
					Properties.IS_FIRST_LAUNCH.setValue(false);
				}
			}
		});

		f.add(closeButton, BorderLayout.SOUTH);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (runOnClose) {
					showUI();
				}
				f.setVisible(false);
			}
		});

		f.setPreferredSize(new Dimension(525, 250));

		f.pack();

		f.setVisible(true);
	}

	private static void showUI() {
		new UI();
	}

	JDesktopPane desktop;

	CameraListSerializer cameraListSerializer = new CameraListSerializer();

	public UI() {
		super(Constants.SOFTWARE_NAME);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		registerSystemTray();

		addWindowStateListener(arg0 -> log.info(arg0.getNewState() + ""));

		setSize(Properties.WIDTH.getIntValue(), Properties.HEIGHT.getIntValue());

		setIconImage(Constants.softwareIcon);

		// Menu

		MenuBar menu = new MenuBar();
		Menu file = new Menu("File");
		MenuItem addNewCamera = new MenuItem("Add new camera");
		addNewCamera.addActionListener(e -> new CreateNewCameraUI(camera -> {
			log.info("Received network camera : " + camera);
			addCamera(camera);
			saveNewCamera(camera);
		}));
		file.add(addNewCamera);

		MenuItem detectLocalCameras = new MenuItem("Detect local cameras");
		detectLocalCameras.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				ArrayList<Integer> validLocalCameras = new ArrayList<>();

				for (int i = 0; i < 10; i++) {

					VideoCapture video = new VideoCapture(i);

					if (video.isOpened()) {
						log.info("Camera " + i + " is valid.");
						validLocalCameras.add(i);

					} else {
						log.info("Camera " + i + " is not valid.");
					}

					video.release();

				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				showDialog("Found " + validLocalCameras.size() + " valid camera(s).");

				for (Integer i : validLocalCameras) {
					addCamera(new SerializedCamera("Camera " + i, i));
				}

			}

		});
		file.add(detectLocalCameras);

		menu.add(file);

		Menu edit = new Menu("Edit");
		MenuItem editProperties = new MenuItem("Edit properties");
		editProperties.addActionListener(e -> showEditPropertiesPanel(false));
		edit.add(editProperties);

		MenuItem showLogger = new MenuItem("Show logger");
		showLogger.addActionListener(e -> showLogger());
		edit.add(showLogger);

		menu.add(edit);

		setMenuBar(menu);

		// End menu bar

		// STATUS BAR

		JPanel statusPanel = new JPanel();
		//statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

		JLabel statusLabel = new JLabel();
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);

		java.util.Timer t = new java.util.Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				statusLabel.setText(" Disk space : " + FileSizeToString.getByteSizeAsString(new File(".").getUsableSpace()));
			}
		}, 0, 5000);

		// End

		desktop = new JDesktopPane();

		try {
			for (SerializedCamera n : cameraListSerializer.get()) {
				addCamera(n);
			}
		} catch (Exception e) {
			log.info("Error with deserialization. Try deleting the file at " + cameraListSerializer.file.getAbsolutePath() + ".");
		}

		add(desktop);
		setLocationRelativeTo(null);
		setVisible(true);

	}

	private void showDialog(String s) {

		log.info(s);
		JOptionPane.showMessageDialog(null, s);

	}

	private void registerSystemTray() {

		if (SystemTray.isSupported()) {

			Image image = Constants.softwareIcon;
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					log.info("Exiting....");

					saveConfig();

					System.exit(0);
				}
			};
			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(exitListener);
			popup.add(defaultItem);
			defaultItem = new MenuItem("Open");
			defaultItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
					setExtendedState(JFrame.NORMAL);
				}
			});
			popup.add(defaultItem);
			TrayIcon trayIcon = new TrayIcon(image, Constants.SOFTWARE_NAME, popup);
			trayIcon.setImageAutoSize(true);

			addWindowStateListener(new WindowStateListener() {
				public void windowStateChanged(WindowEvent e) {

					SystemTray tray = SystemTray.getSystemTray();

					if (e.getNewState() == ICONIFIED) {
						try {
							tray.add(trayIcon);
							setVisible(false);
							log.info("added to SystemTray");
						} catch (AWTException ex) {
							log.info("unable to add to tray");
						}
					}
					if (e.getNewState() == 7) {
						try {
							tray.add(trayIcon);
							setVisible(false);
							log.info("added to SystemTray");
						} catch (AWTException ex) {
							log.info("unable to add to system tray");
						}
					}
					if (e.getNewState() == MAXIMIZED_BOTH) {
						tray.remove(trayIcon);
						setVisible(true);
						log.info("Tray icon removed");
					}
					if (e.getNewState() == NORMAL) {
						tray.remove(trayIcon);
						setVisible(true);
						log.info("Tray icon removed");
					}
				}
			});

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		} else {
			log.info("system tray not supported");
			return;
		}

	}

	private void showLogger() {
		desktop.add(new LoggerWrapper());
	}

	boolean hasChanged = false;

	ArrayList<CameraPanel> cameraFrameList = new ArrayList<>();

	public void addCamera(SerializedCamera n) {

		CameraPanel i = new CameraPanel(n, () -> removeCamera(n));
		i.setVisible(true);

		cameraFrameList.add(i);

		desktop.add(i);

	}

	private void removeCamera(SerializedCamera n) {
		cameraListSerializer.get().remove(n);
		cameraListSerializer.save();
	}

	public void saveNewCamera(SerializedCamera n) {
		cameraListSerializer.get().add(n);
		cameraListSerializer.save();
	}

	private void saveConfig() {

		for (CameraPanel c : cameraFrameList) {
			c.dispose();
		}

		log.info("Saving...");
		cameraListSerializer.save();

		Properties.X.setIntValue(getX());
		Properties.Y.setIntValue(getY());
		Properties.WIDTH.setIntValue(getWidth());
		Properties.HEIGHT.setIntValue(getHeight());

		System.exit(0);
	}

}
