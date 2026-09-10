package gameq.desktop;

import atlantafx.base.theme.PrimerLight;
import gameq.domain.Game;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public final class App extends Application {

    private final AppController controller = new AppController(this);
    private TextField titleInput;
    private Button addButton;
    private ListView<String> gameTitles;
    private Label status;
    private boolean libraryReady;
    private boolean saving;

    @Override
    public void start(Stage stage) {
        setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        titleInput = new TextField();
        titleInput.setPromptText("Game title");
        titleInput.textProperty().addListener((_, _, _) -> updateAddButton());

        addButton = new Button("Add");
        addButton.setGraphic(new FontIcon(Feather.PLUS));
        addButton.setDefaultButton(true);
        addButton.setDisable(true);
        addButton.setOnAction(_ -> controller.addGame(titleInput.getText()));

        gameTitles = new ListView<>();
        gameTitles.setPlaceholder(new Label("No games yet"));

        status = new Label("Opening library...");
        status.setWrapText(true);

        var form = new HBox(8, titleInput, addButton);
        HBox.setHgrow(titleInput, Priority.ALWAYS);

        var heading = new Label("Game library");
        var root = new VBox(12, heading, form, gameTitles, status);
        root.setPadding(new Insets(24));
        VBox.setVgrow(gameTitles, Priority.ALWAYS);

        stage.setTitle("gameq");
        stage.setScene(new Scene(root, 960, 640));
        stage.show();

        controller.start();
    }

    @Override
    public void stop() {
        controller.close();
    }

    void showGames(List<Game> games) {
        gameTitles.getItems().setAll(games.stream().map(Game::title).toList());
        status.setText("");
        libraryReady = true;
        updateAddButton();
        titleInput.requestFocus();
    }

    void showSaving() {
        saving = true;
        updateAddButton();
        status.setText("Saving...");
    }

    void showAddedGame(Game game) {
        gameTitles.getItems().add(game.title());
        titleInput.clear();
        status.setText("");
        saving = false;
        updateAddButton();
        titleInput.requestFocus();
    }

    void showSaveError(String message) {
        showError(message);
        saving = false;
        updateAddButton();
    }

    void showError(String message) {
        status.setText(message);
    }

    private void updateAddButton() {
        addButton.setDisable(!libraryReady || saving || titleInput.getText().isBlank());
    }
}
