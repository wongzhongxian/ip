package clearblue.gui;

import clearblue.Clearblue;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the Clearblue GUI's main window: a scrolling list of chat
 * bubbles above a text field and send button.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Clearblue clearblue;

    private final Image userImage = new Image(MainWindow.class.getResourceAsStream("/images/User.png"));
    private final Image botImage = new Image(MainWindow.class.getResourceAsStream("/images/Bot.png"));

    /**
     * Keeps the scroll pane pinned to the bottom as new dialog boxes are
     * added.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Clearblue} instance this window sends commands to.
     *
     * @param clearblue chatbot instance to wrap
     */
    public void setClearblue(Clearblue clearblue) {
        this.clearblue = clearblue;
    }

    /**
     * Reads the text field, gets Clearblue's reply, and appends both as
     * dialog boxes to the conversation. Closes the window if the command
     * was {@code bye}, after a short pause so the farewell message is
     * visible.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = clearblue.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getClearblueDialog(response, botImage)
        );
        userInput.clear();

        if (clearblue.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
