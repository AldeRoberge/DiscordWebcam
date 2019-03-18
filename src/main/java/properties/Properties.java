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
public abstract class Properties {

	static {
		propertyFile = new PropertyFileManager("discord-webcam.properties");
	}

	private static PropertyFileManager propertyFile;

	//@formatter:off
	public static final Property DISCORD_BOT_CLIENT_ID = new Property("DISCORD_BOT_CLIENT_ID", "Your discord bot client id.", "your-client-id", propertyFile);
	public static final Property DISCORD_BOT_CLIENT_SECRET = new Property("DISCORD_BOT_CLIENT_SECRET", "Your discord client secret.", "your-discord-secret", propertyFile);
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

	public JPanel getPropertiesPanel(List<Property> properties) {

		JPanel jPanel = new JPanel();

		jPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane propertyScrollPane = new JScrollPane();
		jPanel.add(propertyScrollPane, BorderLayout.CENTER);

		JPanel propertyPanel = new JPanel();
		propertyScrollPane.setViewportView(propertyPanel);

		propertyPanel.setLayout(new BoxLayout(propertyPanel, BoxLayout.Y_AXIS));

		for (Property p : properties) {
			propertyPanel.add(p.getEditPropertyPanel());
		}

		return jPanel;

	}

}

