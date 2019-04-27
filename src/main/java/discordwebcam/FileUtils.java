package discordwebcam;

import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileUtils {

	private static org.slf4j.Logger log = LoggerFactory.getLogger(FileUtils.class);

	public static void makeSureFolderExists(String path) {

		if (path == null) {
			log.error("Fatal : Attempting to create folder from null path.");
			return;
		}

		File folder = new File(path);

		if (!(folder.exists())) {
			log.info("Attempting to create folder '" + folder.getAbsolutePath() + "'. Result : '"
					+ folder.mkdir() + "'.");
		}
	}

	// Note : outputfile should have .png in name
	public static boolean saveToFile(BufferedImage image, File outputfile) throws IOException {
		return ImageIO.write(image, "png", outputfile);


	}

}
