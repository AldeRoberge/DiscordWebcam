package discordwebcam.logger;

import alde.commons.util.text.StackTraceToString;
import discordwebcam.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StaticDialog extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public StaticDialog(String title, String message, Exception e) {
		setTitle(title);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 498, 195);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel messagePanel = new JPanel();
		contentPane.add(messagePanel, BorderLayout.CENTER);
		messagePanel.setLayout(new BorderLayout(0, 0));

		JLabel lblInformation = new JLabel(message);
		lblInformation.setHorizontalAlignment(SwingConstants.CENTER);
		messagePanel.add(lblInformation);

		JScrollPane errorPanel = new JScrollPane();
		contentPane.add(errorPanel, BorderLayout.SOUTH);

		JTextArea textArea = new JTextArea();

		errorPanel.setViewportView(textArea);

		if (e != null) {
			setIconImage(Constants.errorIcon);
			textArea.setText(StackTraceToString.sTTS(e));
		} else {
			setIconImage(Constants.informationIcon);
			errorPanel.setVisible(false);

		}
	}

	public static void display(String title, String message) {
		display(title, message, null);
	}

	public static void display(String title, String message, Exception e) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StaticDialog frame = new StaticDialog(title, message, e);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
