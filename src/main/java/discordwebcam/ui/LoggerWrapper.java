package discordwebcam.ui;

import alde.commons.console.Console;
import alde.commons.console.ConsoleAction;
import alde.commons.logger.LoggerPanel;
import discordwebcam.Constants;

import javax.swing.*;
import java.awt.*;

public class LoggerWrapper extends JInternalFrame {

	private static LoggerPanel l = new LoggerPanel();
	private static Console c = new Console();

	public LoggerWrapper() {

		super("Logger", true, true, true, true);

		setSize(new Dimension(200, 200));
		setLocation(20, 20);

		setFrameIcon(new ImageIcon(Constants.loggerIcon));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(l, BorderLayout.CENTER);
		mainPanel.add(c, BorderLayout.SOUTH);

		this.getContentPane().add(mainPanel);

		setVisible(true);

	}

	public static void addAction(String keyword, Runnable action) {
		c.addAction(new ConsoleAction() {
			@Override
			public void accept(String command) {
				action.run();
			}

			@Override
			protected String getDescription() {
				return null;
			}

			@Override
			public String[] getKeywords() {
				return new String[]{keyword};
			}
		});
	}

}
