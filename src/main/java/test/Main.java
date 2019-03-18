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

		JFrame.setDefaultLookAndFeelDecorated(false);

		if (Properties.IS_FIRST_LAUNCH.getBooleanValue()) {

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
		UI u = new UI();
	}

	public void startDiscordBot() {
		Discord d = new Discord(Properties.DISCORD_BOT_TOKEN.getValue());
		d.start();




	}

}
