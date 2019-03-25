package discordwebcam.ui;

import alde.commons.util.jtextfield.UtilityJTextField;
import alde.commons.util.window.UtilityJFrame;
import discordwebcam.Constants;
import discordwebcam.camera.SerializedCamera;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class CreateNewCameraUI extends UtilityJFrame {

	private final Consumer<SerializedCamera> getNewCamera;
	private JPanel contentPane;
	private UtilityJTextField nameField;
	private UtilityJTextField networkField;

	/**
	 * Create the frame.
	 */
	public CreateNewCameraUI(Consumer<SerializedCamera> getNewCamera) {

		this.getNewCamera = getNewCamera;

		setResizable(false);
		setTitle("Add a new network camera");

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setIconImage(Constants.gearIcon);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				acceptCamera();
			}
		});

		setBounds(100, 100, 400, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		Component hS = Box.createHorizontalStrut(5);
		contentPane.add(hS, BorderLayout.WEST);

		Component hS2 = Box.createHorizontalStrut(5);
		contentPane.add(hS2, BorderLayout.EAST);

		Component vS = Box.createVerticalStrut(5);
		contentPane.add(vS, BorderLayout.NORTH);

		Component vS2 = Box.createVerticalStrut(5);
		contentPane.add(vS2, BorderLayout.SOUTH);

		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		JPanel okayPanel = new JPanel();
		mainPanel.add(okayPanel, BorderLayout.SOUTH);
		okayPanel.setLayout(new BorderLayout(0, 0));

		Component horizontalStrut = Box.createHorizontalStrut(120);
		okayPanel.add(horizontalStrut, BorderLayout.WEST);

		Component horizontalStrut_1 = Box.createHorizontalStrut(120);
		okayPanel.add(horizontalStrut_1, BorderLayout.EAST);

		Component verticalStrut = Box.createVerticalStrut(5);
		okayPanel.add(verticalStrut, BorderLayout.NORTH);

		JPanel configPanelContainer = new JPanel();
		mainPanel.add(configPanelContainer, BorderLayout.CENTER);
		configPanelContainer.setLayout(new BoxLayout(configPanelContainer, BoxLayout.Y_AXIS));

		JPanel configPanel = new JPanel();
		configPanelContainer.add(configPanel);
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));

		JPanel configPanel2 = new JPanel();
		configPanel.add(configPanel2);
		configPanel2.setLayout(new BorderLayout(0, 0));

		JPanel labelPanel = new JPanel();
		configPanel2.add(labelPanel, BorderLayout.WEST);
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

		JPanel panel_8 = new JPanel();
		labelPanel.add(panel_8);

		JLabel lblName = new JLabel("Name : ");
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		panel_8.add(lblName);

		JPanel panel_9 = new JPanel();
		labelPanel.add(panel_9);

		JLabel lblNetworkAddress = new JLabel("Network address : ");
		lblNetworkAddress.setHorizontalAlignment(SwingConstants.CENTER);
		panel_9.add(lblNetworkAddress);

		JPanel textFieldPanel = new JPanel();
		configPanel2.add(textFieldPanel, BorderLayout.CENTER);
		textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.Y_AXIS));

		JPanel namePanel = new JPanel();
		textFieldPanel.add(namePanel);

		nameField = new UtilityJTextField("Unnamed camera");
		nameField.setHint("Enter a camera name");
		nameField.setToolTipText("Helps to identify between different cameras.");
		nameField.allowAutocomplete();
		nameField.allowMemory();
		nameField.setColumns(20);

		JPanel networkAddressPanel = new JPanel();
		textFieldPanel.add(networkAddressPanel);

		networkField = new UtilityJTextField("http://192.168.0.107:8080/video?x.mjpeg");
		networkField.setHint("Enter a camera address");
		networkField.setToolTipText("A mjpeg video feed address.");
		networkAddressPanel.add(networkField);
		networkField.setColumns(20);

		JButton button = new JButton("Okay");
		button.addActionListener(arg0 -> acceptCamera());
		okayPanel.add(button);

		setVisible(true);

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				CreateNewCameraUI frame = new CreateNewCameraUI(c -> System.out.println("Network camera : " + c));
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void acceptCamera() {
		getNewCamera.accept(new SerializedCamera(nameField.getText(), networkField.getText()));
		dispose();
		setVisible(false);
	}

}
