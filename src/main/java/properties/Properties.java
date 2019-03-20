package properties;

import alde.commons.properties.BooleanProperty;
import alde.commons.properties.IntProperty;
import alde.commons.properties.Property;
import alde.commons.properties.PropertyFileManager;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of a Properties class
 */
public class Properties {



	static {
		propertyFile = new PropertyFileManager("discord-webcam.properties");
	}

	private static PropertyFileManager propertyFile;

	//@formatter:off
	public static final BooleanProperty IS_FIRST_LAUNCH = new BooleanProperty("IS_FIRST_LAUNCH", "Displays the 'edit properties' UI on launch", true, propertyFile);
	public static final Property DISCORD_BOT_TOKEN = new Property("DISCORD_BOT_TOKEN", "Your discord bot token", "your-token", propertyFile);

	public static final Property DISCORD_CHANNEL_ID = new Property("DISCORD_CHANNEL_ID", "Discord channel ID", "discord-channel-id", propertyFile);

	public static final IntProperty WIDTH = new IntProperty("WIDTH", "The program's width", 800, propertyFile);
	public static final IntProperty HEIGHT = new IntProperty("HEIGHT", "The program height", 600, propertyFile);

	public static final IntProperty X = new IntProperty("X", "The program's x location on screen", 20, propertyFile);
	public static final IntProperty Y = new IntProperty("Y", "The program's y location on screen", 20, propertyFile);

	public static final Property SAVE_IMAGES_FOLDER = new Property("SAVE_IMAGES_FOLDER", "The folder to save images to", "C:\\Users\\rotmg\\Desktop", propertyFile);

	public static final IntProperty REFRESH_DISK_SPACE_EVERY = new IntProperty("REFRESH_DISK_SPACE_EVERY", "Time (in ms) between refreshes of free disk space. Set to 0 to not refresh.", 5000, propertyFile);

	//@formatter:on

	/**
	 * Gets all the Properties of the superclass using reflection
	 *
	 * @return List<Property> list of properties
	 */
	public static List<Property> getProperties() {
		List<Property> properties = new ArrayList<>();

		for (Field f : Properties.class.getDeclaredFields()) {
			if (f.getType().getSuperclass().equals(Property.class) || f.getType().equals(Property.class)) {
				try {
					properties.add((Property) f.get(Properties.class));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}

	public static JPanel getPropertiesPanel() {

		JPanel jPanel = new JPanel();

		jPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane propertyScrollPane = new JScrollPane();
		jPanel.add(propertyScrollPane, BorderLayout.CENTER);

		JPanel propertyPanel = new JPanel();
		propertyScrollPane.setViewportView(propertyPanel);

		propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.Y_AXIS));

		for (Property p : getProperties()) {
			propertyPanel.add(p.getEditPropertyPanel());
		}

		return jPanel;

	}

}

