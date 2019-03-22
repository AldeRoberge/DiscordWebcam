package discordwebcam.ui;

import alde.commons.console.Console;
import alde.commons.logger.LoggerPanel;
import discordwebcam.Constants;

import javax.swing.*;
import java.awt.*;

public class LoggerWrapper extends JInternalFrame {

	public LoggerWrapper() {

		super("Logger", true, true, true, true);

		setSize(new Dimension(200, 200));
		setLocation(20, 20);

		setFrameIcon(new ImageIcon(Constants.loggerIcon));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(new LoggerPanel(), BorderLayout.CENTER);
		mainPanel.add(new Console(), BorderLayout.SOUTH);

		this.getContentPane().add(mainPanel);

		setVisible(true);

	}

}
