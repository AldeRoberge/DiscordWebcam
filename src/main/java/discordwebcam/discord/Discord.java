package discordwebcam.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import discordwebcam.properties.Properties;
import discordwebcam.Constants;
import discordwebcam.detection.MotionDetectionEvent;

import java.awt.*;

public class Discord {

	static DiscordApi api;

	static {
		api = new DiscordApiBuilder().setToken(Properties.DISCORD_BOT_TOKEN.getValue()).login().join();
	}

	public static void notifyDetection(MotionDetectionEvent e) {
		api.getTextChannelById(Properties.DISCORD_CHANNEL_ID.getValue()).ifPresent(textChannel -> textChannel.sendMessage(getEmbedFromMotionDetection(e)));
	}

	public static EmbedBuilder getEmbedFromMotionDetection(MotionDetectionEvent e) {
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
	}

}
