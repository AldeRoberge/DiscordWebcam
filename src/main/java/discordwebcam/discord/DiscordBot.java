package discordwebcam.discord;

import alde.commons.logger.LoggerListener;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import discordwebcam.logger.StaticDialog;
import discordwebcam.properties.Properties;
import discordwebcam.ui.UI;
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

	public static void init() {
		try {
			api = new DiscordApiBuilder().setToken(Properties.DISCORD_BOT_TOKEN.getValue()).login().join();

			// Print the invite url of your bot
			log.info("You can invite the bot by using the following url: " + api.createBotInvite());

			LoggerListener.addListener(event -> {
				if (event.getLevel() != Level.DEBUG) {
					sendMessage(formatLogToSimpleMessage(event));
				}
			});

			// Add a listener which answers with "Pong!" if someone writes "!ping"
			api.addMessageCreateListener(event -> {

				if (!event.getMessage().getAuthor().isYourself()) {
					UI.getConsole().receivedCommand(event.getMessageContent());
				}

			});

		} catch (Exception e) {
			StaticDialog.display("Error with Discord Bot", "Error connecting Discord bot. \n Try changing the Discord Bot Token in '" + Properties.getPropertiesFilePath() + "'.", e);
		}
	}

	private static String formatLogToSimpleMessage(ILoggingEvent event) {

		String level = event.getLevel().levelStr;
		String name = event.getLoggerName();
		String line = event.getMessage();

		String message = "[" + level + "] " + name + " : " + line;

		log.info(message);

		return message;

	}

	/*public static void notifyDetection(MotionDetectionEvent e) {
		api.getTextChannelById(Properties.DISCORD_CHANNEL_ID.getValue()).ifPresent(textChannel -> textChannel.sendMessage(getEmbedFromMotionDetection(e)));
	}*/

	public static void sendMessage(EmbedBuilder embedBuilder) {
		try {

			api.getTextChannelById(Properties.DISCORD_CHANNEL_ID.getValue()).ifPresent(textChannel -> textChannel.sendMessage(embedBuilder));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendMessage(String string) {
		try {
			api.getTextChannelById(Properties.DISCORD_CHANNEL_ID.getValue()).ifPresent(textChannel -> textChannel.sendMessage(string));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
