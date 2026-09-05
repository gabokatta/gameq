package gameq;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class GameqApplication extends Application {
    @Override
    public void start(Stage stage) {
        var root = new StackPane(new Label("gameq"));
        stage.setTitle("gameq");
        stage.setScene(new Scene(root, 960, 640));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
