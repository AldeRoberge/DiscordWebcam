package discordwebcam.ui;

import com.sun.javafx.webkit.WebConsoleListener;
import discordwebcam.Constants;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedBrowser extends Application {

	static Logger log = LoggerFactory.getLogger(EmbeddedBrowser.class);


	@Override
	public void start(Stage primaryStage) {
		WebView webView = new WebView();
		webView.getEngine().load(EmbeddedBrowser.class.getResource("/app/index.html").toString());
		webView.setContextMenuEnabled(false);
		primaryStage.setScene(new Scene(webView, 1000, 800));
		primaryStage.setTitle("Help");
		primaryStage.getIcons().add(SwingFXUtils.toFXImage(Constants.softwareIcon, null));
		primaryStage.show();
		WebConsoleListener.setDefaultListener((wv, message, lineNumber, sourceId) -> {
			log.info("[Embedded Browser] : " + message + "[at " + lineNumber + "]");
		});
	}

}
