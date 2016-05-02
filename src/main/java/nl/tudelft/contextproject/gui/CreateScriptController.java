package main.java.nl.tudelft.contextproject.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.java.nl.tudelft.contextproject.ContextTFP;

import java.io.IOException;

/**
 * Controller class for the script creation screen.
 * 
 * @author Steven Meijer
 */
public class CreateScriptController {

    @FXML
    private Button btnAddEvent;
    @FXML
    private Button btnBack;
    @FXML
    private ChoiceBox<Integer> cbCameraNr;
    @FXML
    private TextArea txtNewEvent;
    @FXML
    private TextArea txtEvents;
    @FXML
    private TextField txtTimestamp;

    @FXML
    private void initialize() {
        txtEvents.setEditable(false);

        // TODO Get from settings file
        cbCameraNr.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8));

        btnBack.setOnAction((event) -> {
                MenuController.show();
            });

        btnAddEvent.setOnAction((event) -> {

            // TODO Put in database
                Boolean errors = false;
                if (txtTimestamp.getText().isEmpty()) {
                    txtTimestamp.setStyle("-fx-border-color: red;");
                    errors = true;
                } else {
                    txtTimestamp.setStyle("");
                }

                if (txtNewEvent.getText().isEmpty()) {
                    txtNewEvent.setStyle("-fx-border-color: red;");
                    errors = true;
                } else {
                    txtNewEvent.setStyle("");
                }

                if (cbCameraNr.getSelectionModel().getSelectedItem() == null) {
                    cbCameraNr.setStyle("-fx-border-color: red;");
                    errors = true;
                } else {
                    cbCameraNr.setStyle("");
                }

                if (!errors) {
                    txtEvents.appendText(txtTimestamp.getText() + " [Camera: "
                            + cbCameraNr.getSelectionModel().getSelectedItem() + "] - " + txtNewEvent.getText() + "\n");
                    txtNewEvent.clear();
                    txtTimestamp.clear();
                }
            });
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/CreateScriptView.fxml"));
            AnchorPane createScriptView = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(createScriptView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
