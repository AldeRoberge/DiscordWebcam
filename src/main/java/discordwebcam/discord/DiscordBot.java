package discordwebcam.discord;

import discordwebcam.logger.StaticDialog;
import discordwebcam.properties.Properties;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static class to send detection messages on Discord
 */
public class DiscordBot {

	static Logger log = LoggerFactory.getLogger(DiscordBot.class);

	static DiscordApi api;

	static {
		try {
			api = new DiscordApiBuilder().setToken(Properties.DISCORD_BOT_TOKEN.getValue()).login().join();

			// Print the invite url of your bot
			log.info("You can invite the bot by using the following url: " + api.createBotInvite());

		} catch (Exception e) {
			StaticDialog.display("Error with Discord Bot", "Error connecting Discord bot. \n Try changing the Discord Bot Token in '" + Properties.getPropertiesFilePath() + "'.", e);
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
