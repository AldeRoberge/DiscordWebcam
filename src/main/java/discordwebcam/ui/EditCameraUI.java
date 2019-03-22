package discordwebcam.ui;

import alde.commons.util.window.UtilityJFrame;
import com.sun.istack.internal.Nullable;
import discordwebcam.Constants;
import discordwebcam.camera.SerializedCamera;
import discordwebcam.discord.Discord;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.HashMap;

public class EditCameraUI extends UtilityJFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				EditCameraUI frame = new EditCameraUI(new SerializedCamera("ip", "adress"), null, null);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	static HashMap<String, Integer> interpolationTypes = new HashMap<>();

	static {
		interpolationTypes.put("INTER_NEAREST", Imgproc.INTER_NEAREST);
		interpolationTypes.put("INTER_LINEAR", Imgproc.INTER_LINEAR);
		interpolationTypes.put("INTER_CUBIC", Imgproc.INTER_CUBIC);
		interpolationTypes.put("INTER_AREA", Imgproc.INTER_AREA);
		interpolationTypes.put("INTER_LANCZOS4", Imgproc.INTER_LANCZOS4);
		interpolationTypes.put("INTER_LINEAR_EXACT", Imgproc.INTER_LINEAR_EXACT);
	}

	/**
	 * Create the frame.
	 */
	public EditCameraUI(final SerializedCamera n, @Nullable final Runnable runOnClose, @Nullable final Runnable runOnRotate) {

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

		setBounds(100, 100, 730, 265);
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

		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Discord.sendMessage("Test button was pressed at '" + new Date() + "'.");
			}
		});
		publishOnDiscordPanel.add(btnTest);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		listPanel.add(panel_1);

		JCheckBox chckbxDownscaleQuality = new JCheckBox("Downscale quality");
		chckbxDownscaleQuality.addActionListener(e -> n.downscaleQuality = chckbxDownscaleQuality.isSelected());
		chckbxDownscaleQuality.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(chckbxDownscaleQuality);

		JCheckBox chckbxDownscaleInPreview = new JCheckBox("Downscale in preview");
		chckbxDownscaleInPreview
				.addActionListener(e -> n.downscalePreviewQuality = chckbxDownscaleInPreview.isSelected());
		panel_1.add(chckbxDownscaleInPreview);

		JSlider downscaleAmount = new JSlider();
		downscaleAmount.setValue(1);
		downscaleAmount.setMinimum(1);
		downscaleAmount.setMaximum(100);
		downscaleAmount.addChangeListener(e -> n.downScaleAmount = downscaleAmount.getValue());

		panel_1.add(downscaleAmount);

		JComboBox<String> interpolationType = new JComboBox<String>();

		for (String s : interpolationTypes.keySet()) {
			interpolationType.addItem(s);
		}

		interpolationType.addActionListener(
				e -> n.interpolationType = interpolationTypes.get(interpolationType.getSelectedItem()));

		panel_1.add(interpolationType);

		JPanel panel_2 = new JPanel();
		listPanel.add(panel_2);

		JButton btnRotateButton = new JButton("Rotate button");
		btnRotateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (n.rotateDeg == 360) {
					n.rotateDeg = 0;
				}

				n.rotateDeg += 90;

				if (runOnRotate != null) {
					runOnRotate.run();
				}

			}
		});
		btnRotateButton.setIcon(new ImageIcon(Constants.rotateIcon));
		panel_2.add(btnRotateButton);

		chckbxPublishOnDiscord.addActionListener(e -> n.sendOnDiscord = chckbxPublishOnDiscord.isSelected());

		labelPanel.add(new JLabel("Discord", JLabel.TRAILING));

		JLabel lblNewLabel = new JLabel("Downscale quality");
		labelPanel.add(lblNewLabel);

		JLabel lblRotate = new JLabel("Rotate");
		labelPanel.add(lblRotate);

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
