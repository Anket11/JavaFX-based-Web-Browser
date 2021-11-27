package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ApplicationMain extends Application {

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        root.setCenter(new WebBrowserController());

        Scene scene = new Scene(root, getVisualScreenWidth(), getVisualScreenHeight());
        // Scene scene = new Scene(root, 1950, 800);

        primaryStage.setTitle("Catalyst Browser");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static double getVisualScreenWidth() {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static double getVisualScreenHeight() {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
