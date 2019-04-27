package discordwebcam;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/*
	 * Used by save to file
	 */
	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-SSS");// dd/MM/yyyy
		Date now = new Date();
		return sdfDate.format(now);
	}

}
