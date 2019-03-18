package properties;

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
	public static final Property DISCORD_BOT_TOKEN = new Property("DISCORD_BOT_TOKEN", "Your discord bot token.", "your-token", propertyFile);
	//@formatter:on

	/**
	 * Gets all the Properties of the superclass using reflection
	 *
	 * @return List<Property> list of perfectpitch.properties
	 */
	public static List<Property> getProperties() {
		List<Property> properties = new ArrayList<>();

		for (Field f : Properties.class.getDeclaredFields()) {
			if (f.getType().equals(Property.class)) {
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

