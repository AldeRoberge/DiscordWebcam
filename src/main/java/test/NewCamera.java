package test;


import alde.commons.util.jtextfield.UtilityJTextField;
import alde.commons.util.window.UtilityJFrame;
import sun.text.normalizer.Utility;

import javax.rmi.CORBA.Util;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class NewCamera extends UtilityJFrame {

	private final Consumer<NetworkCamera> getNewCamera;
	private JPanel contentPane;
	private UtilityJTextField nameField;
	private UtilityJTextField networkField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewCamera frame = new NewCamera(new Consumer<NetworkCamera>() {
						public void accept(NetworkCamera c) {
							System.out.println("Network camera : " + c);
						}
					});
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
	public NewCamera(Consumer<NetworkCamera> getNewCamera) {

		this.getNewCamera = getNewCamera;

		setResizable(false);
		setTitle("Add new network camera");

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setIconImage(Constants.gearIcon);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				acceptCamera(null);
			}
		});

		setBounds(100, 100, 386, 172);
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

		nameField = new UtilityJTextField();
		nameField.setHint("Enter a camera name");
		nameField.setToolTipText("Helps to identify between different cameras.");
		namePanel.add(nameField);
		nameField.setColumns(20);

		JPanel networkAddressPanel = new JPanel();
		textFieldPanel.add(networkAddressPanel);

		networkField = new UtilityJTextField();
		networkField.setHint("Enter a camera address");
		networkField.setToolTipText("Example : http://192.168.0.107:8080/video?x.mjpeg");
		networkAddressPanel.add(networkField);
		networkField.setColumns(20);

		JButton button = new JButton("Okay");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				acceptCamera(new NetworkCamera(nameField.getText(), networkField.getText()));
			}
		});
		okayPanel.add(button);

		setVisible(true);

	}

	private void acceptCamera(NetworkCamera camera) {
		getNewCamera.accept(camera);
		dispose();
		setVisible(false);
	}

}
