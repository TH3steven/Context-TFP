package main.java.nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.java.nl.tudelft.contextproject.ContextTFP;

import java.io.IOException;

/**
 * Controller class for the main menu. This class controls the actions to be taken
 * when one of the menu buttons is clicked.
 * 
 * @author Steven Meijer
 */
public class MenuController {

    @FXML private Button btnCreateScript;
    @FXML private Button btnLoadScript;
    @FXML private Button btnPreview;
    @FXML private Button btnPresets;
    @FXML private Button btnApply;
    @FXML private TextField numberOfCameras;

    @FXML
    private void initialize() {
        btnCreateScript.setOnAction((event) -> {
            CreateScriptController.show();
        });
        btnPreview.setOnAction((event) -> {
            PreviewController.show();
        });
        btnPresets.setOnAction((event) -> {
            PresetController.show();
        });
        btnApply.setOnAction((event) -> {
            boolean exception = false;
            if (!numberOfCameras.getText().isEmpty()) {
                try {
                    Integer.parseInt(numberOfCameras.getText());
                    
                } catch (NumberFormatException e) {
                    exception = true;
                }            
            } else {
                exception = true;
            }
            
            if (exception) {
                numberOfCameras.setStyle("-fx-border-color: red;");
            } else {
                numberOfCameras.setStyle("");
                System.out.println(numberOfCameras.getText());
            } 
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
