package main.java.nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import main.java.nl.tudelft.contextproject.ContextTFP;
import main.java.nl.tudelft.contextproject.saveLoad.LoadScript;

import java.io.File;
import java.io.IOException;

/**
 * Controller class for the main menu. This class controls the actions to be taken
 * when one of the menu buttons is clicked.
 * 
 * @since 0.1
 */
public class MenuController {

    @FXML private Button btnCreateScript;
    @FXML private Button btnLoadScript;
    @FXML private Button btnPreview;
    @FXML private Button btnPresets;
    @FXML private Button btnEditScript;
    @FXML private Button btnDirector;
    @FXML private Button btnCameraman;

    @FXML private TextField numberOfCameras;

    @FXML private void initialize() {
        btnCreateScript.setOnAction((event) -> {
            CreateScriptController.show();
        });

        btnLoadScript.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select script to use");
            File file = fileChooser.showOpenDialog(((Node) event.getTarget()).getScene().getWindow());

            if (file != null) {
                try {
                    LoadScript.setLoadLocation(file.getAbsolutePath());
                    ContextTFP.setScript(LoadScript.load());

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Info Dialog");
                    alert.setHeaderText("Loading script was succesful!");
                    alert.setContentText("Succesful load of script: " + file.getName());

                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Loading script was unsuccesful!");
                    alert.setContentText("Error when trying to load script at location: " 
                            + file.getAbsolutePath());

                    alert.showAndWait();
                }
            }
        });

        btnPreview.setOnAction((event) -> {
            PreviewController.show();
        });

        btnPresets.setOnAction((event) -> {
            PresetController.show();
        });

        btnCameraman.setOnAction((event) -> {
            CameraLiveController.show();
        });
        
        btnDirector.setOnAction((event) -> {
            CameraLiveController.show();
        });
        
        btnEditScript.setOnAction((event) -> {
            CreateScriptController.setFill(true);
            CreateScriptController.show();
        });
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/MenuOverview.fxml"));
            AnchorPane menuOverview = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(menuOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
