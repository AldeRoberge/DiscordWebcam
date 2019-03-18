package test;

import alde.commons.util.window.UtilityJFrame;
import opencv.CameraPanel;
import properties.Properties;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
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

		setSize(Properties.WIDTH.getIntValue(), Properties.HEIGHT.getIntValue());

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

	boolean hasChanged = false;

	public void addCamera(NetworkCamera n) {

		CameraPanel panel = new CameraPanel(n);

		JInternalFrame cameraPanelFrame = new JInternalFrame(n.name, true, true, true, true);
		cameraPanelFrame.setSize(n.width, n.height);
		cameraPanelFrame.setLocation(n.x, n.y);
		cameraPanelFrame.setVisible(true);

		cameraPanelFrame.addInternalFrameListener(new InternalFrameAdapter(){
			public void internalFrameClosing(InternalFrameEvent e) {
				removeCamera(n);
			}
		});

		cameraPanelFrame.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				super.componentMoved(e);

				n.x = cameraPanelFrame.getX();
				n.y = cameraPanelFrame.getY();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);

				n.height = cameraPanelFrame.getHeight();
				n.width = cameraPanelFrame.getWidth();
			}
		});

		cameraPanelFrame.setFrameIcon(Constants.cameraIcon);

		cameraPanelFrame.add(panel, BorderLayout.CENTER);

		desktop.add(cameraPanelFrame);

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
