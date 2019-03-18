package test;

import alde.commons.util.window.UtilityJFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditNetworkCamera extends UtilityJFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EditNetworkCamera frame = new EditNetworkCamera(new NetworkCamera("ip", "adress"), null);
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
	public EditNetworkCamera(final NetworkCamera n, final Runnable runOnClose) {
		setTitle("Edit");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close(runOnClose);
			}
		});


		setBounds(100, 100, 530, 246);
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
				n.name = cameraName.getText();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				n.name = cameraName.getText();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
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

		chckbxMotionDetection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				n.motionDetection = chckbxMotionDetection.isSelected();
			}
		});

		JSlider thresholdSlider = new JSlider();
		thresholdSlider.setPaintTicks(true);
		thresholdSlider.setMajorTickSpacing(51);
		thresholdSlider.setMinorTickSpacing(17);
		thresholdSlider.setPaintLabels(true);
		thresholdSlider.setSnapToTicks(true);
		thresholdSlider.setValue(n.threshold);
		thresholdSlider.setMaximum(Constants.MAX_THRESHOLD);
		allowMotionDetectionPanel.add(thresholdSlider);

		thresholdSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				n.threshold = thresholdSlider.getValue();
			}
		});

		JPanel publishOnDiscordPanel = new JPanel();
		listPanel.add(publishOnDiscordPanel);
		publishOnDiscordPanel.setLayout(new BoxLayout(publishOnDiscordPanel, BoxLayout.X_AXIS));

		JCheckBox chckbxPublishOnDiscord = new JCheckBox("Publish on Discord");
		chckbxPublishOnDiscord.setSelected(n.publishOnDiscord);
		chckbxPublishOnDiscord.setHorizontalAlignment(SwingConstants.CENTER);
		publishOnDiscordPanel.add(chckbxPublishOnDiscord);

		chckbxPublishOnDiscord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				n.publishOnDiscord = chckbxPublishOnDiscord.isSelected();
			}
		});

		labelPanel.add(new JLabel("Discord", JLabel.TRAILING));

		panel.add(mainPanel, BorderLayout.CENTER);

		JButton btnClose = new JButton("Close");

		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				close(runOnClose);

			}
		});

		contentPane.add(btnClose, BorderLayout.SOUTH);

		setVisible(true);

	}

	private void close(Runnable runOnClose) {
		runOnClose.run();
		dispose();
	}

}
