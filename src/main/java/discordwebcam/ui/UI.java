package discordwebcam.ui;

import alde.commons.console.Console;
import alde.commons.console.ConsoleAction;
import alde.commons.logger.LoggerPanel;
import alde.commons.util.file.FileEditor;
import alde.commons.util.file.FileSizeToString;
import alde.commons.util.text.StackTraceToString;
import alde.commons.util.window.UtilityJFrame;
import com.sun.javafx.webkit.WebConsoleListener;
import discordwebcam.Constants;
import discordwebcam.camera.CreateNewCameraUI;
import discordwebcam.camera.SerializedCamera;
import discordwebcam.logger.StaticDialog;
import discordwebcam.camera.CameraPanel;
import discordwebcam.properties.Properties;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.TimerTask;

public class UI extends UtilityJFrame {

	private static Logger log = LoggerFactory.getLogger(UI.class);

	/*
	 * Loads the native drivers for OpenCV
	 */
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.loadLibrary("opencv_ffmpeg342_64"); //crucial to use IP Camera
	}

	JDesktopPane desktop;
	CameraListSerializer cameraListSerializer = new CameraListSerializer();
	ArrayList<CameraPanel> cameraFrameList = new ArrayList<>();

	public UI() {
		super(Constants.SOFTWARE_NAME);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		registerSystemTray();

		setSize(Properties.UI_WIDTH.getIntValue(), Properties.UI_HEIGHT.getIntValue());

		setIconImage(Constants.softwareIcon);

		// Menu bar

		MenuBar menu = new MenuBar();

		// FILE

		Menu file = new Menu("File");
		MenuItem addNewCamera = new MenuItem("Add a network camera");
		addNewCamera.addActionListener(e -> new CreateNewCameraUI(camera -> {
			log.info("Received network camera : " + camera);
			addCamera(camera);
			addNewCamera(camera);
		}));
		file.add(addNewCamera);

		MenuItem detectLocalCameras = new MenuItem("Detect local cameras");
		detectLocalCameras.addActionListener(e -> {
			detectLocalCameras();
		});
		file.add(detectLocalCameras);

		menu.add(file);

		// EDIT

		Menu edit = new Menu("Edit");
		MenuItem editProperties = new MenuItem("Edit properties");
		editProperties.addActionListener(e -> showEditPropertiesPanel(false));
		edit.add(editProperties);

		MenuItem showLogger = new MenuItem("Show logger");
		showLogger.addActionListener(e -> showLogger());

		edit.add(showLogger);

		menu.add(edit);

		// HELP

		Menu help = new Menu("Help");
		MenuItem showHelp = new MenuItem("Show help");
		showHelp.addActionListener(e -> showHelp());
		help.add(showHelp);
		
		MenuItem sendDetection = new MenuItem("Send fake detection image");
		sendDetection.addActionListener(e -> sendFakeDetection());
		help.add(sendDetection);

		menu.add(help);

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

		performStartup();

	}

	private void sendFakeDetection() {
		for (CameraPanel c : cameraFrameList) {
			c.takeScreenshotAndSendToDiscord();
		}
	}

	/**
	 * Main entry of the program
	 */
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

	/**
	 * Launches a JavaFX app that embeds the Help website at resources/app/index.html
	 */
	private void showHelp() {
		try {
			Application.launch(EmbeddedBrowser.class, null, null);
		} catch (Exception e) {
			e.printStackTrace();

			log.error("Error with EmbeddedBrowser : ", e);
		}
	}

	private static void showEditPropertiesPanel(boolean isFirstLaunch) {
		UtilityJFrame f = new UtilityJFrame();

		f.setIconImage(Constants.gearIcon);

		f.setTitle("Edit properties");

		if (isFirstLaunch) {
			f.add(Properties.getEditPropertiesPanel(Properties.DISCORD_BOT_TOKEN, Properties.DISCORD_CHANNEL_ID), BorderLayout.CENTER);
		} else {
			f.add(Properties.getEditPropertiesPanel(), BorderLayout.CENTER);
		}

		Button closeButton = new Button("Close");
		closeButton.addActionListener(e -> {
			log.info("Close");
			f.setVisible(false);

			if (isFirstLaunch) {
				showUI();
				Properties.IS_FIRST_LAUNCH.setValue(false);
			}
		});

		f.add(closeButton, BorderLayout.SOUTH);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (isFirstLaunch) {
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

	private void performStartup() {
		if (Properties.SHOW_LOGGER_ON_STARTUP.getBooleanValue()) {
			showLogger();
		}

		if (cameraFrameList.size() == 0 && Properties.GET_LOCAL_CAMERAS_ON_STARTUP.getBooleanValue()) {
			detectLocalCameras();
		}
	}

	private void detectLocalCameras() {
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

		if (validLocalCameras.size() == 0) {
			StaticDialog.display("Warning", "No camera found.");
		} else {

			for (Integer i : validLocalCameras) {
				addCamera(new SerializedCamera("Camera " + i, i));
			}
		}
	}

	/**
	 * Registers behavior for when the application is minimised
	 */
	private void registerSystemTray() {

		if (SystemTray.isSupported()) {

			Image image = Constants.softwareIcon;
			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(e -> {
				log.info("Exiting....");
				saveConfigBeforeClosing();
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

	public void addNewCamera(SerializedCamera n) {
		cameraListSerializer.get().add(n);
		cameraListSerializer.save();
	}

	private void saveConfigBeforeClosing() {

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


class CameraListSerializer {

	private static org.slf4j.Logger log = LoggerFactory.getLogger(FileEditor.class);

	public final File file = new File(new File(".") + File.separator + "cameras.serialized");

	private ArrayList<SerializedCamera> list;

	public CameraListSerializer() {
		list = get();
	}

	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			log.info("Error while serialising cameras.");
			ioe.printStackTrace();
		}
	}

	public ArrayList<SerializedCamera> get() {

		if (list != null) {
			return list;
		} else {
			if (file.exists() && !(file.length() == 0)) {
				try {
					FileInputStream fis = new FileInputStream(file);

					ObjectInputStream ois = new ObjectInputStream(fis);
					list = (ArrayList<SerializedCamera>) ois.readObject();
					ois.close();
					fis.close();

				} catch (IOException | ClassNotFoundException e) {
					log.error(StackTraceToString.sTTS(e));
					e.printStackTrace();

					list = new ArrayList<>();
				}
			} else {
				log.warn("File " + file.getAbsolutePath() + " is empty or does not exist!");
				list = new ArrayList<>();
			}

			return list;

		}

	}

}


class EmbeddedBrowser extends Application {

	private static Logger log = LoggerFactory.getLogger(EmbeddedBrowser.class);

	@Override
	public void start(Stage primaryStage) {
		WebView webView = new WebView();
		webView.getEngine().load(EmbeddedBrowser.class.getResource("/app/index.html").toString());
		webView.setContextMenuEnabled(false);
		primaryStage.setScene(new Scene(webView, 1000, 800));
		primaryStage.setTitle("Help");
		primaryStage.getIcons().add(SwingFXUtils.toFXImage(Constants.softwareIcon, null));
		primaryStage.show();
		WebConsoleListener.setDefaultListener((wv, message, lineNumber, sourceId) -> {
			log.info("[Embedded Browser] : " + message + "[at " + lineNumber + "]");
		});
	}

}


class LoggerWrapper extends JInternalFrame {

	private static LoggerPanel l = new LoggerPanel();
	private static alde.commons.console.Console c = new Console();

	public LoggerWrapper() {

		super("Logger", true, true, true, true);

		setSize(new Dimension(400, 300));
		setLocation(20, 20);

		setFrameIcon(new ImageIcon(Constants.loggerIcon));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(l, BorderLayout.CENTER);
		mainPanel.add(c, BorderLayout.SOUTH);

		this.getContentPane().add(mainPanel);

		setVisible(true);

	}

	public static void addAction(String keyword, Runnable action) {
		c.addAction(new ConsoleAction() {
			@Override
			public void accept(String command) {
				action.run();
			}

			@Override
			protected String getDescription() {
				return null;
			}

			@Override
			public String[] getKeywords() {
				return new String[]{keyword};
			}
		});
	}

}
