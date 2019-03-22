package discordwebcam.ui;

import alde.commons.util.file.FileSizeToString;
import alde.commons.util.window.UtilityJFrame;
import discordwebcam.CameraListSerializer;
import discordwebcam.Constants;
import discordwebcam.camera.SerializedCamera;
import discordwebcam.logger.StaticDialog;
import discordwebcam.opencv.CameraPanel;
import discordwebcam.properties.Properties;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.TimerTask;

public class UI extends UtilityJFrame {

	/*
	 * Loads the native drivers for OpenCV
	 */
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary("opencv_ffmpeg342_64"); //crucial to use IP Camera
	}

	private static Logger log = LoggerFactory.getLogger(UI.class);

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.error("Could not set look and feel. ", e);
		}

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
		closeButton.addActionListener(e -> {
			log.info("Close");
			f.setVisible(false);

			if (runOnClose) {
				showUI();
				Properties.IS_FIRST_LAUNCH.setValue(false);
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

		setSize(Properties.UI_WIDTH.getIntValue(), Properties.UI_HEIGHT.getIntValue());

		setIconImage(Constants.softwareIcon);

		// Menu

		MenuBar menu = new MenuBar();
		Menu file = new Menu("File");
		MenuItem addNewCamera = new MenuItem("Add a network camera");
		addNewCamera.addActionListener(e -> new CreateNewCameraUI(camera -> {
			log.info("Received network camera : " + camera);
			addCamera(camera);
			saveNewCamera(camera);
		}));
		file.add(addNewCamera);

		MenuItem detectLocalCameras = new MenuItem("Detect local cameras");
		detectLocalCameras.addActionListener(e -> {

			ArrayList<Integer> validLocalCameras = new ArrayList<>();

			log.debug("Looking local cameras from 0 to " + Properties.MAX_LOCAL_CAMERA_ID_CHECK.getIntValue() + " cameras.");

			for (int i = 0; i < 10; i++) {

				VideoCapture video = new VideoCapture(i);

				if (video.isOpened()) {
					log.info("Found camera with id " + i + ".");
					validLocalCameras.add(i);
				}

				video.release();

			}

			/*try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}*/

			if (validLocalCameras.size() == 0) {
				StaticDialog.display("Warning", "No camera found.");
			} else {

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
		statusPanel.setBackground(new Color(240, 240, 240));

		statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

		JLabel statusLabel = new JLabel();
		statusPanel.setForeground(Color.WHITE);
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);

		java.util.Timer t = new java.util.Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				statusLabel.setText(" Free disk space : " + FileSizeToString.getByteSizeAsString(new File(".").getUsableSpace()));

				if (Properties.UI_REFRESH_DISK_SPACE_EVERY.getIntValue() == 0) {
					cancel();
				}

			}
		}, 0, Properties.UI_REFRESH_DISK_SPACE_EVERY.getIntValue());

		// End

		desktop = new JDesktopPane();
		desktop.setBackground(Color.BLACK);

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

		if (Properties.SHOW_LOGGER_ON_STARTUP.getBooleanValue()) {
			showLogger();
		}

	}

	private void registerSystemTray() {

		if (SystemTray.isSupported()) {

			Image image = Constants.softwareIcon;
			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(e -> {
				log.info("Exiting....");
				saveConfig();
				System.exit(0);
			});
			popup.add(defaultItem);
			defaultItem = new MenuItem("Open");
			defaultItem.addActionListener(e -> {
				setVisible(true);
				setExtendedState(JFrame.NORMAL);
			});
			popup.add(defaultItem);
			TrayIcon trayIcon = new TrayIcon(image, Constants.SOFTWARE_NAME, popup);
			trayIcon.setImageAutoSize(true);

			addWindowStateListener(e -> {

				SystemTray tray = SystemTray.getSystemTray();

				if (e.getNewState() == ICONIFIED) {
					try {
						tray.add(trayIcon);
						setVisible(false);
						log.info("Added to SystemTray.");
					} catch (AWTException ex) {
						log.info("Unable to add to tray.");
					}
				} else if (e.getNewState() == 7) {
					try {
						tray.add(trayIcon);
						setVisible(false);
						log.info("Added to SystemTray.");
					} catch (AWTException ex) {
						log.info("Unable to add to system tray.");
					}
				} else if (e.getNewState() == MAXIMIZED_BOTH) {
					tray.remove(trayIcon);
					setVisible(true);
					log.info("Tray icon removed.");
				} else if (e.getNewState() == NORMAL) {
					tray.remove(trayIcon);
					setVisible(true);
					log.info("Tray icon removed.");
				}
			});

			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		} else {
			log.info("System tray is not supported.");
		}

	}

	private void showLogger() {
		desktop.add(new LoggerWrapper());
	}

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

		Properties.UI_X.setIntValue(getX());
		Properties.UI_Y.setIntValue(getY());
		Properties.UI_WIDTH.setIntValue(getWidth());
		Properties.UI_HEIGHT.setIntValue(getHeight());

		System.exit(0);
	}

}
