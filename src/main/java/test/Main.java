package test;

import discord.Discord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import properties.Properties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

	static Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.error("Could not set look and feel. ", e);
		}

		if (true) {

		//if (Properties.IS_FIRST_LAUNCH.getBooleanValue()) {


			JFrame f = new JFrame();
			f.setTitle("Set properties");
			f.add(Properties.getPropertiesPanel(), BorderLayout.CENTER);
			Button closeButton = new Button("Close");
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					log.info("Next...");
					f.setVisible(false);

					Properties.IS_FIRST_LAUNCH.setValue(false);

					run();
				}

			});



			f.add(closeButton, BorderLayout.SOUTH);

			f.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					run();
				}
			});

			f.pack();

			f.setVisible(true);

		} else {
			run();
		}

	}

	private void run() {



		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("JInternalFrame Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);

		// Menu

		MenuBar menu = new MenuBar();
		Menu file = new Menu("File");
		menu.add(file);

		Menu edit = new Menu("Edit");
		menu.add(edit);

		frame.setMenuBar(menu);

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

		frame.add(desktop);
		frame.setVisible(true);








	}


	public void startDiscordBot() {
		Discord d = new Discord(Properties.DISCORD_BOT_TOKEN.getValue());
		d.start();


	}

}
