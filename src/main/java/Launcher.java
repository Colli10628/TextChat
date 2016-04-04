package textchat;
import javafx.application.Application;
import java.net.URL;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class Launcher extends Application{
	public static void main(String[] args){
		launch();
	}
	public void start(Stage primaryStage){
		primaryStage.setTitle("TextChat Launcher");
		Parent root = null;
		String sceneFile = "Launcher.fxml";
		URL url = null;
		try{
			url = getClass().getClassLoader().getResource(sceneFile);
			root = FXMLLoader.load(url);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		}
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
}
