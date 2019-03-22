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

	public static void sendMessage(EmbedBuilder embedBuilder) {
		api.getTextChannelById(Properties.DISCORD_CHANNEL_ID.getValue()).ifPresent(textChannel -> textChannel.sendMessage(embedBuilder));
	}

	public static void sendMessage(String string) {
		api.getTextChannelById(Properties.DISCORD_CHANNEL_ID.getValue()).ifPresent(textChannel -> textChannel.sendMessage(string));
	}

}
