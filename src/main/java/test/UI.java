package test;

import alde.commons.util.window.UtilityJFrame;
import opencv.CameraPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UI extends UtilityJFrame {

	public static void main(String[] args) {
		new UI();
	}

	JDesktopPane desktop;

	CameraListSerializer cameraListSerializer = new CameraListSerializer();

	public UI() {
		super("Discord Webcam");

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				saveConfig();
			}
		});

		setSize(500, 300);

		// Menu

		MenuBar menu = new MenuBar();
		Menu file = new Menu("File");
		MenuItem addNewCamera = new MenuItem("Add new camera");
		addNewCamera.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new NewCamera(networkCamera -> {
					System.out.println("Received network camera : " + networkCamera);
					addCamera(networkCamera);
					saveNewCamera(networkCamera);
				});
			}
		});
		file.add(addNewCamera);
		menu.add(file);

		Menu edit = new Menu("Edit");
		menu.add(edit);

		setMenuBar(menu);

		// End menu bar

		desktop = new JDesktopPane();

		int attempt = 0;

		while (true) {

			try {
				System.out.println("Serialiser : " + cameraListSerializer.get().size());

				for (NetworkCamera n : cameraListSerializer.get()) {
					addCamera(n);
				}

				break;
			} catch (Exception e) {
				System.out.println("Error with serialization. Deleting the serialization file at " + cameraListSerializer.file + ". Answer : " + cameraListSerializer.file.delete());

				attempt++;
			}

			if (attempt > 2) {
				System.out.println("Could not get deserialization to work. Exiting.");

				System.exit(-1);
			}

		}

		add(desktop);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void addCamera(NetworkCamera n) {

		CameraPanel panel = new CameraPanel(n);

		JInternalFrame if1 = new JInternalFrame(n.name, true, true, true, true);
		if1.setSize(300, 300);
		if1.setLocation(n.x, n.y);
		if1.setVisible(true);

		if1.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				n.x = if1.getX();
				n.y = if1.getY();
			}
		});

		if1.add(panel, BorderLayout.CENTER);

		desktop.add(if1);

	}

	public void saveNewCamera(NetworkCamera n) {
		cameraListSerializer.get().add(n);
		cameraListSerializer.save();
	}

	private void saveConfig() {

		System.out.println("Saving...");
		cameraListSerializer.save();
		System.exit(0);
	}

}
