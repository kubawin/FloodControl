


import javafx.application.Application;
import javafx.stage.Stage;

public class Program extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		FloodControl floodControl = new FloodControl( stage );
		floodControl.run();
		
	}
}
