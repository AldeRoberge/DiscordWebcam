package discordwebcam.logger;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import alde.commons.util.text.StackTraceToString;
import discordwebcam.Constants;

public class StaticDialog extends JFrame {

	private JPanel contentPane;

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

	/**
	 * Create the frame.
	 */
	public StaticDialog(String title, String message, Exception e) {
		setTitle(title);




		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 134);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel messagePanel = new JPanel();
		contentPane.add(messagePanel, BorderLayout.CENTER);

		JLabel lblInformation = new JLabel(message);
		lblInformation.setHorizontalAlignment(SwingConstants.CENTER);
		messagePanel.add(lblInformation);

		if (e != null) {

			setIconImage(Constants.errorIcon);

			JPanel errorPanel = new JPanel();
			contentPane.add(errorPanel, BorderLayout.SOUTH);
			errorPanel.setLayout(new BorderLayout(0, 0));

			JTextArea textArea = new JTextArea();

			textArea.setText(StackTraceToString.sTTS(e));

			errorPanel.add(textArea, BorderLayout.NORTH);

		} else {
			setIconImage(Constants.informationIcon);
		}
	}

}
