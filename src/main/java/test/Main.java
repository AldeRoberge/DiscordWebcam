package test;

import discord.Discord;
import properties.Properties;

import javax.swing.*;
import java.awt.*;

public class Main {

	public static void main(String[] args) {

		Discord d = new Discord(Properties.DISCORD_BOT_TOKEN.getValue());
		d.start();


		


		JFrame f = new JFrame();
		f.setTitle("Edit properties");
		f.add(Properties.getPropertiesPanel(), BorderLayout.CENTER);

		f.setVisible(true);


	}

}
