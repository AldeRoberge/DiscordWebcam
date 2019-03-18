package test.ui;

import alde.commons.util.window.UtilityJFrame;
import discord.Discord;
import opencv.CameraPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.Properties;
import test.CameraListSerializer;
import test.Constants;
import test.NetworkCamera;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class UI extends UtilityJFrame {

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
			run();
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
					run();
					Properties.IS_FIRST_LAUNCH.setValue(false);
				}
			}
		});

		f.add(closeButton, BorderLayout.SOUTH);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (runOnClose) {
					run();
				}
				f.setVisible(false);
			}
		});

		f.setPreferredSize(new Dimension(525, 250));

		f.pack();

		f.setVisible(true);
	}

	private static void run() {
		UI u = new UI();
	}

	public void startDiscordBot() {
		Discord d = new Discord(Properties.DISCORD_BOT_TOKEN.getValue());
		d.start();

	}

	JDesktopPane desktop;

	CameraListSerializer cameraListSerializer = new CameraListSerializer();

	public UI() {
		super(Constants.SOFTWARE_NAME);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				saveConfig();
			}
		});

		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent arg0) {
				System.out.println(arg0.getNewState());
			}
		});

		setSize(Properties.WIDTH.getIntValue(), Properties.HEIGHT.getIntValue());

		setIconImage(Constants.softwareIcon);

		// Menu

		MenuBar menu = new MenuBar();
		Menu file = new Menu("File");
		MenuItem addNewCamera = new MenuItem("Add new camera");
		addNewCamera.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateNewCameraUI(networkCamera -> {
					System.out.println("Received network camera : " + networkCamera);
					addCamera(networkCamera);
					saveNewCamera(networkCamera);
				});
			}
		});
		file.add(addNewCamera);
		menu.add(file);

		Menu edit = new Menu("Edit");
		MenuItem editProperties = new MenuItem("Edit properties");
		editProperties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showEditPropertiesPanel(false);
			}
		});
		edit.add(editProperties);

		MenuItem showLogger = new MenuItem("Show logger");
		showLogger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showLogger();
			}
		});
		edit.add(showLogger);

		menu.add(edit);

		setMenuBar(menu);

		// End menu bar

		desktop = new JDesktopPane();

		try {
			for (NetworkCamera n : cameraListSerializer.get()) {
				addCamera(n);
			}
		} catch (Exception e) {
			System.out.println("Error with serialization. Try deleting the serialization file at " + cameraListSerializer.file.getAbsolutePath() + ".");
		}

		add(desktop);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void showLogger() {
		desktop.add(new LoggerWrapper());
	}

	boolean hasChanged = false;

	ArrayList<CameraPanel> cameraFrameList = new ArrayList<>();

	public void addCamera(NetworkCamera n) {

		CameraPanel i = new CameraPanel(n, () -> removeCamera(n));
		i.setVisible(true);

		cameraFrameList.add(i);

		desktop.add(i);

	}

	private void removeCamera(NetworkCamera n) {
		cameraListSerializer.get().remove(n);
		cameraListSerializer.save();
	}

	public void saveNewCamera(NetworkCamera n) {
		cameraListSerializer.get().add(n);
		cameraListSerializer.save();
	}

	private void saveConfig() {

		System.out.println("Saving...");
		cameraListSerializer.save();

		Properties.X.setIntValue(getX());
		Properties.Y.setIntValue(getY());
		Properties.WIDTH.setIntValue(getWidth());
		Properties.HEIGHT.setIntValue(getHeight());

		System.exit(0);
	}

}
