package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application
{
	@Override
	public void start(Stage primaryStage) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/MainScreen.fxml"));
		Parent root = loader.load();
//		MainController mainController = loader.<MainController>load();
//		mainController.setStage(primaryStage);
		
		primaryStage.setTitle("Mediaplayer");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
