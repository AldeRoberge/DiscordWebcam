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

		setIconImage(Constants.softwareIcon);

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
		MenuItem editProperties = new MenuItem("Edit properties");
		editProperties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.showEditPropertiesPanel(false);
			}
		});
		edit.add(editProperties);
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

		InternalNetworkCameraFrame i = new InternalNetworkCameraFrame(n);
		i.setVisible(true);

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

	private class InternalNetworkCameraFrame extends JInternalFrame {
		InternalNetworkCameraFrame(NetworkCamera n) {

			super(n.name, true, true, true, true);

			CameraPanel panel = new CameraPanel(n);

			setSize(n.width, n.height);
			setLocation(n.x, n.y);

			setResizable(true);
			setLayout(new BorderLayout());

			addInternalFrameListener(new InternalFrameAdapter() {
				@Override
				public void internalFrameActivated(InternalFrameEvent e) {
					super.internalFrameActivated(e);
					panel.receivedFocus();
				}

				@Override
				public void internalFrameDeactivated(InternalFrameEvent e) {
					super.internalFrameDeactivated(e);
					panel.lostFocus();
				}
			});

			addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosing(InternalFrameEvent e) {
					removeCamera(n);
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

			add(panel, BorderLayout.CENTER);

		}

	}
}
