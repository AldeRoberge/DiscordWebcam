package test;

import alde.commons.util.window.UtilityJFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class UI extends UtilityJFrame {

	public static void main(String[] args) {
		new UI();
	}

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
				});
			}
		});
		file.add(addNewCamera);
		menu.add(file);

		Menu edit = new Menu("Edit");
		menu.add(edit);

		setMenuBar(menu);

		// End menu bar

		JDesktopPane desktop = new JDesktopPane();

		JInternalFrame if1 = new JInternalFrame("Frame 1", true, true, true, true);
		if1.setSize(200, 200);
		desktop.add(if1);

		JInternalFrame if2 = new JInternalFrame("Frame 2", true, true, true, true);
		if2.setSize(200, 200);
		desktop.add(if2);

		if1.setLocation(20, 20);
		if1.setVisible(true);
		if2.setLocation(40, 40);
		if2.setVisible(true);

		add(desktop);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void saveConfig() {

		System.out.println("Saving...");

		//TODO implement

		System.exit(0);
	}

}
