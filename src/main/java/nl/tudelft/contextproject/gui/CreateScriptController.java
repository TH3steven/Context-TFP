package main.java.nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import main.java.nl.tudelft.contextproject.ContextTFP;

import java.io.IOException;

public class CreateScriptController {
    
    @FXML private Button btnAddEvent;
    @FXML private TextArea txtNewEvent;
    @FXML private TextArea txtEvents;
    @FXML private TextField txtTimestamp;
    
    @FXML
    private void initialize() {
        txtEvents.setEditable(false);
        
        btnAddEvent.setOnAction((event) -> {
            
            //TODO Put in database
            txtEvents.appendText(txtTimestamp.getText() + " - " + txtNewEvent.getText() + "\n");
            txtNewEvent.clear();
            txtTimestamp.clear();
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
