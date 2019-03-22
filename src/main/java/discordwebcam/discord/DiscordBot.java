package discordwebcam.discord;

import discordwebcam.Constants;
import discordwebcam.camera.SerializedCamera;
import discordwebcam.detection.MotionDetectionEvent;
import discordwebcam.logger.StaticDialog;
import discordwebcam.properties.Properties;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

/**
 * Static class to send detection messages on DiscordBot
 */
public class DiscordBot {

	static DiscordApi api;

	static {
		try {
			api = new DiscordApiBuilder().setToken(Properties.DISCORD_BOT_TOKEN.getValue()).login().join();
		} catch (Exception e) {
			StaticDialog.display("Error with DiscordBot Bot", "Error connecting DiscordBot bot. \n Try changing the DiscordBot Bot Token in '" + Properties.getPropertiesFilePath() + "'.", e);
		}
	}


	/*public static void notifyDetection(MotionDetectionEvent e) {
		api.getTextChannelById(Properties.DISCORD_CHANNEL_ID.getValue()).ifPresent(textChannel -> textChannel.sendMessage(getEmbedFromMotionDetection(e)));
	}*/

	/*public static EmbedBuilder getEmbedFromMotionDetection(SerializedCamera e) {
		return new EmbedBuilder()
				.setTitle("Camera " + e.cameraName + " detected motion.")
				.setDescription(e.detectionDate.toLocaleString())
				.setAuthor(Constants.SOFTWARE_NAME)
				//.addField("A field", "Some text inside the field")
				//.addInlineField("An inline field", "More text")
				//.addInlineField("Another inline field", "Even more text")
				.setColor(Color.RED)
				//.setFooter("Footer", "https://cdn.discordapp.com/embed/avatars/1.png")
				.setImage(e.imageFile);
	}*/

	public static void sendMessage(String string) {
		api.getTextChannelById(Properties.DISCORD_CHANNEL_ID.getValue()).ifPresent(textChannel -> textChannel.sendMessage(string));
	}

}
