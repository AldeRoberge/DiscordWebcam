package discordwebcam.ui;

import alde.commons.util.window.UtilityJFrame;
import com.sun.istack.internal.Nullable;
import discordwebcam.Constants;
import discordwebcam.camera.SerializedCamera;
import discordwebcam.discord.Discord;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	static Logger log = LoggerFactory.getLogger(EditCameraUI.class);

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
	public EditCameraUI(final SerializedCamera n, @Nullable final Runnable runOnClose,
			@Nullable final Runnable runOnRotate) {

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

		setBounds(100, 100, 693, 324);
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

		labelPanel.add(new JLabel("Camera", JLabel.TRAILING));

		labelPanel.add(new JLabel("Motion", JLabel.TRAILING));

		labelPanel.add(new JLabel("Discord", JLabel.TRAILING));

		JLabel label = new JLabel("Preview");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		labelPanel.add(label);

		JLabel lblQuality = new JLabel("Quality");
		lblQuality.setHorizontalAlignment(SwingConstants.RIGHT);
		labelPanel.add(lblQuality);

		JLabel label_1 = new JLabel("Rotate");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		labelPanel.add(label_1);

		JPanel listPanel = new JPanel(new GridLayout(0, 1, 2, 2));
		mainPanel.add(listPanel, BorderLayout.CENTER);

		JPanel cameraNamePanel = new JPanel();
		listPanel.add(cameraNamePanel);
		cameraNamePanel.setLayout(new BoxLayout(cameraNamePanel, BoxLayout.X_AXIS));

		JLabel cameraNameLabel = new JLabel("Name : ");
		cameraNamePanel.add(cameraNameLabel);
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

		JPanel motionDetectionPanel = new JPanel();
		listPanel.add(motionDetectionPanel);
		motionDetectionPanel.setLayout(new BoxLayout(motionDetectionPanel, BoxLayout.X_AXIS));

		JCheckBox chckbxMotionDetection = new JCheckBox("Allow motion detection");
		chckbxMotionDetection.setSelected(n.motionDetection);
		motionDetectionPanel.add(chckbxMotionDetection);

		chckbxMotionDetection.addActionListener(e -> n.motionDetection = chckbxMotionDetection.isSelected());

		JCheckBox chckbxShowMotionDetection = new JCheckBox("Show motion detection boxes");
		chckbxShowMotionDetection
				.addActionListener(e -> n.showMotionDetectionInPreview = chckbxShowMotionDetection.isSelected());
		motionDetectionPanel.add(chckbxShowMotionDetection);

		JSlider detectionThresholdSlider = new JSlider();
		detectionThresholdSlider.setPaintTicks(true);
		detectionThresholdSlider.setMajorTickSpacing(51);
		detectionThresholdSlider.setMinorTickSpacing(17);
		detectionThresholdSlider.setPaintLabels(true);
		detectionThresholdSlider.setSnapToTicks(true);
		detectionThresholdSlider.setValue(n.motionDetectionThreshold);
		detectionThresholdSlider.setMaximum(Constants.MAX_THRESHOLD);
		motionDetectionPanel.add(detectionThresholdSlider);

		detectionThresholdSlider.addChangeListener(e -> n.motionDetectionThreshold = detectionThresholdSlider.getValue());

		JPanel discordPanel = new JPanel();
		listPanel.add(discordPanel);
		discordPanel.setLayout(new BoxLayout(discordPanel, BoxLayout.X_AXIS));

		JCheckBox chckbxPublishOnDiscord = new JCheckBox("Publish on Discord");
		chckbxPublishOnDiscord.setSelected(n.sendOnDiscord);
		chckbxPublishOnDiscord.setHorizontalAlignment(SwingConstants.CENTER);
		discordPanel.add(chckbxPublishOnDiscord);

		JButton btnTest = new JButton("Send test message");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Discord.sendMessage("Test button was pressed at '" + new Date() + "'.");
			}
		});

		Component horizontalStrut = Box.createHorizontalStrut(5);
		discordPanel.add(horizontalStrut);
		discordPanel.add(btnTest);

		JPanel previewPanel = new JPanel();
		listPanel.add(previewPanel);
		previewPanel.setLayout(new BorderLayout(0, 0));

		JSlider slider = new JSlider();
		slider.setMinimum(1);
		slider.setMinorTickSpacing(100);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(500);
		slider.setMaximum(5000);
		slider.addChangeListener(e -> n.timeBetweenPreviewRepaint = slider.getValue());
		previewPanel.add(slider);

		JCheckBox chckbxNewCheckBox = new JCheckBox("Don't repaint when window is not focused");
		chckbxNewCheckBox.addActionListener(e -> n.repaintPreviewWhenOutOfFocus = chckbxNewCheckBox.isSelected());
		previewPanel.add(chckbxNewCheckBox, BorderLayout.WEST);

		JPanel qualityPanel = new JPanel();
		FlowLayout fl_qualityPanel = (FlowLayout) qualityPanel.getLayout();
		fl_qualityPanel.setAlignment(FlowLayout.LEFT);
		listPanel.add(qualityPanel);

		JCheckBox chckbxDownscaleQuality = new JCheckBox("Downscale quality");
		chckbxDownscaleQuality.addActionListener(e -> n.downscaleQuality = chckbxDownscaleQuality.isSelected());
		chckbxDownscaleQuality.setHorizontalAlignment(SwingConstants.LEFT);
		qualityPanel.add(chckbxDownscaleQuality);

		JCheckBox chckbxDownscaleInPreview = new JCheckBox("Downscale in preview");
		chckbxDownscaleInPreview
				.addActionListener(e -> n.downscalePreviewQuality = chckbxDownscaleInPreview.isSelected());
		qualityPanel.add(chckbxDownscaleInPreview);

		JSlider downscaleAmount = new JSlider();
		downscaleAmount.setValue(1);
		downscaleAmount.setMinimum(1);
		downscaleAmount.setMaximum(100);
		downscaleAmount.addChangeListener(e -> n.downScaleAmount = downscaleAmount.getValue());

		qualityPanel.add(downscaleAmount);

		JComboBox<String> interpolationType = new JComboBox<String>();
		interpolationType.setMaximumRowCount(10);

		for (String s : interpolationTypes.keySet()) {
			interpolationType.addItem(s);
		}

		interpolationType.addActionListener(
				e -> n.interpolationType = interpolationTypes.get(interpolationType.getSelectedItem()));

		qualityPanel.add(interpolationType);

		JPanel rotatePanel = new JPanel();
		listPanel.add(rotatePanel);

		JButton btnRotateButton = new JButton("Rotate");
		btnRotateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (n.rotateDeg == 360) {
					n.rotateDeg = 0;
				}

				n.rotateDeg += 90;

				log.info("Flipping camera '" + n.name + "' for '" + n.rotateDeg + "' degrees.");

				if (runOnRotate != null) {
					runOnRotate.run();
				}

			}
		});
		btnRotateButton.setIcon(new ImageIcon(Constants.rotateIcon));
		rotatePanel.add(btnRotateButton);

		chckbxPublishOnDiscord.addActionListener(e -> n.sendOnDiscord = chckbxPublishOnDiscord.isSelected());

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
