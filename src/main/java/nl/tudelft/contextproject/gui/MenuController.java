package main.java.nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import main.java.nl.tudelft.contextproject.ContextTFP;

import java.io.IOException;

/**
 * Controller class for the main menu.
 * 
 * @author Steven Meijer
 */
public class MenuController {
    
    @FXML private Button btnCreateScript;
    @FXML private Button preview;
    
    @FXML
    private void initialize() {
        btnCreateScript.setOnAction((event) -> {
            CreateScriptController.show();
        });
        preview.setOnAction((event) -> {
            PreviewController.show();
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
