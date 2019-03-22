package discordwebcam.ui;

import alde.commons.util.window.UtilityJFrame;
import discordwebcam.Constants;
import discordwebcam.camera.SerializedCamera;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditCameraUI extends UtilityJFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				EditCameraUI frame = new EditCameraUI(new SerializedCamera("ip", "adress"), null);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EditCameraUI(final SerializedCamera n, final Runnable runOnClose) {
	
		if (n.name.equals("")) {
			setTitle("Edit untitled camera");
		} else {
			setTitle("Edit camera '" + n.name + "'");
		}
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close(runOnClose);
			}
		});

		setIconImage(Constants.gearIcon);

		setBounds(100, 100, 530, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		// details for a File
		JPanel mainPanel = new JPanel(new BorderLayout(4, 2));
		mainPanel.setBorder(new EmptyBorder(0, 6, 0, 6));

		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 2, 2));
		mainPanel.add(labelPanel, BorderLayout.WEST);

		JPanel listPanel = new JPanel(new GridLayout(0, 1, 2, 2));
		mainPanel.add(listPanel, BorderLayout.CENTER);

		labelPanel.add(new JLabel("Name", JLabel.TRAILING));

		JPanel cameraNamePanel = new JPanel();
		listPanel.add(cameraNamePanel);
		cameraNamePanel.setLayout(new BoxLayout(cameraNamePanel, BoxLayout.X_AXIS));
		JTextField cameraName = new JTextField(n.name);
		cameraNamePanel.add(cameraName);

		cameraName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateName();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateName();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateName();
			}

			private void updateName() {
				n.name = cameraName.getText();
			}

		});

		labelPanel.add(new JLabel("Motion detection", JLabel.TRAILING));

		JPanel allowMotionDetectionPanel = new JPanel();
		listPanel.add(allowMotionDetectionPanel);
		allowMotionDetectionPanel.setLayout(new BoxLayout(allowMotionDetectionPanel, BoxLayout.X_AXIS));

		JCheckBox chckbxMotionDetection = new JCheckBox("Allow motion detection");
		chckbxMotionDetection.setSelected(n.motionDetection);
		allowMotionDetectionPanel.add(chckbxMotionDetection);

		chckbxMotionDetection.addActionListener(e -> n.motionDetection = chckbxMotionDetection.isSelected());

		JSlider thresholdSlider = new JSlider();
		thresholdSlider.setPaintTicks(true);
		thresholdSlider.setMajorTickSpacing(51);
		thresholdSlider.setMinorTickSpacing(17);
		thresholdSlider.setPaintLabels(true);
		thresholdSlider.setSnapToTicks(true);
		thresholdSlider.setValue(n.threshold);
		thresholdSlider.setMaximum(Constants.MAX_THRESHOLD);
		allowMotionDetectionPanel.add(thresholdSlider);

		thresholdSlider.addChangeListener(e -> n.threshold = thresholdSlider.getValue());

		JPanel publishOnDiscordPanel = new JPanel();
		listPanel.add(publishOnDiscordPanel);
		publishOnDiscordPanel.setLayout(new BoxLayout(publishOnDiscordPanel, BoxLayout.X_AXIS));

		JCheckBox chckbxPublishOnDiscord = new JCheckBox("Publish on Discord");
		chckbxPublishOnDiscord.setSelected(n.sendOnDiscord);
		chckbxPublishOnDiscord.setHorizontalAlignment(SwingConstants.CENTER);
		publishOnDiscordPanel.add(chckbxPublishOnDiscord);

		chckbxPublishOnDiscord.addActionListener(e -> n.sendOnDiscord = chckbxPublishOnDiscord.isSelected());

		labelPanel.add(new JLabel("Discord", JLabel.TRAILING));

		panel.add(mainPanel, BorderLayout.CENTER);

		JButton btnClose = new JButton("Close");

		btnClose.setSelected(true);

		btnClose.addActionListener(e -> close(runOnClose));

		contentPane.add(btnClose, BorderLayout.SOUTH);

		setAlwaysOnTop(true);
		setVisible(true);

	}

	private void close(Runnable runOnClose) {
		
		if (runOnClose != null) {
			runOnClose.run();
		}
		
		dispose();
	}

}
