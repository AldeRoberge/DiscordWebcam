package test;

import alde.commons.util.file.FileEditor;
import alde.commons.util.text.StackTraceToString;
import org.slf4j.LoggerFactory;
import test.camera.SerializedCamera;

import java.io.*;
import java.util.ArrayList;

public class CameraListSerializer {

	private static org.slf4j.Logger log = LoggerFactory.getLogger(FileEditor.class);

	public final File file = new File(new File(".") + File.separator + "cameras.serialized");

	ArrayList<SerializedCamera> list;

	public CameraListSerializer() {
		list = get();
	}

	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			log.info("Error while serialising cameras.");
			ioe.printStackTrace();
		}
	}

	public ArrayList<SerializedCamera> get() {

		if (list != null) {
			return list;
		} else {
			if (file.exists() && !(file.length() == 0)) {
				try {
					FileInputStream fis = new FileInputStream(file);

					ObjectInputStream ois = new ObjectInputStream(fis);
					list = (ArrayList<SerializedCamera>) ois.readObject();
					ois.close();
					fis.close();

				} catch (IOException | ClassNotFoundException e) {
					log.error(StackTraceToString.sTTS(e));
					e.printStackTrace();

					list = new ArrayList<>();
				}
			} else {
				log.warn("File " + file.getAbsolutePath() + " is empty or does not exist!");
				list = new ArrayList<>();
			}

			return list;

		}

	}

}