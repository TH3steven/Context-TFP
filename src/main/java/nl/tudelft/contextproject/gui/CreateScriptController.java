package main.java.nl.tudelft.contextproject.gui;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import main.java.nl.tudelft.contextproject.ContextTFP;
import main.java.nl.tudelft.contextproject.model.Event;

import java.io.IOException;

/**
 * Controller class for the script creation screen.
 * 
 * @author Steven Meijer
 */
public class CreateScriptController {

    @FXML private Button btnBack;

    @FXML private TableView<Event> tableEvents;
    @FXML private TableColumn<Event, String> tShot;
    @FXML private TableColumn<Event, String> tCamera;
    @FXML private TableColumn<Event, String> tPreset;
    @FXML private TableColumn<Event, String> tEvent;
    @FXML private TableColumn<Event, String> tAdd;

    @FXML
    private void initialize() {

        btnBack.setOnAction((event) -> {
            MenuController.show();
        });

        final ObservableList<Event> data =
                FXCollections.observableArrayList(
                        new Event("Jacob", "1", "2", "kaas"),
                        new Event("Kaas", "2", "5", "is lekker")
                );
        
        tableEvents.setItems(data);

        tShot.setCellValueFactory(
                new PropertyValueFactory<Event, String>("shot"));

        tCamera.setCellValueFactory(
                new PropertyValueFactory<Event, String>("camera"));

        tPreset.setCellValueFactory(
                new PropertyValueFactory<Event, String>("preset"));

        tEvent.setCellValueFactory(
                new PropertyValueFactory<Event, String>("event"));

        //..

    }

    //    @FXML private Button btnAddEvent;
    //    @FXML private Button btnBack;
    //    @FXML private ChoiceBox<Integer> cbCameraNr;
    //    @FXML private TextArea txtEvents;
    //    @FXML private TextField txtNewEvent;
    //    @FXML private TextField txtTimestamp;
    //
    //    @FXML
    //    private void initialize() {
    //        txtEvents.setEditable(false);
    //
    //        // TODO Get from settings file
    //        cbCameraNr.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8));
    //
    //        btnBack.setOnAction((event) -> {
    //            MenuController.show();
    //        });
    //
    //        btnAddEvent.setOnAction((event) -> {
    //
    //            Boolean errors = false;
    //
    //            if (txtTimestamp.getText().isEmpty()) {
    //                txtTimestamp.setStyle("-fx-border-color: red;");
    //                errors = true;
    //            } else {
    //                txtTimestamp.setStyle("");
    //            }
    //
    //            if (txtNewEvent.getText().isEmpty()) {
    //                txtNewEvent.setStyle("-fx-border-color: red;");
    //                errors = true;
    //            } else {
    //                txtNewEvent.setStyle("");
    //            }
    //
    //            if (cbCameraNr.getSelectionModel().getSelectedItem() == null) {
    //                cbCameraNr.setStyle("-fx-border-color: red;");
    //                errors = true;
    //            } else {
    //                cbCameraNr.setStyle("");
    //            }
    //
    //            if (!errors) {
    //                // TODO Put in database
    //                txtEvents.appendText(txtTimestamp.getText() + " [Camera: "
    //                        + cbCameraNr.getSelectionModel().getSelectedItem() + "] - " + txtNewEvent.getText() + "\n");
    //                txtNewEvent.clear();
    //                txtTimestamp.clear();
    //            }
    //        });
    //    }

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
