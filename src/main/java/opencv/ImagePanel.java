package opencv;

import javax.swing.*;
import java.awt.*;

class ImagePanel extends JPanel {
	private Image img;

	public ImagePanel(Image img) {
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
		setBorder(BorderFactory.createLineBorder(Color.black));
	}

	public void updateImage(Image img) {
		this.img = img;
		validate();
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
}
