package discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Discord {

	private final String BOT_TOKEN;

	public Discord(String BOT_TOKEN) {
		this.BOT_TOKEN = BOT_TOKEN;
	}

	public void start() {
		DiscordApi api = new DiscordApiBuilder().setToken(BOT_TOKEN).login().join();

		// Add a listener which answers with "Pong!" if someone writes "!ping"
		api.addMessageCreateListener(event -> {
			if (event.getMessageContent().equalsIgnoreCase("!ping")) {
				event.getChannel().sendMessage("Pong!");
			}
		});

		// Print the invite url of your bot
		System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
	}



}
