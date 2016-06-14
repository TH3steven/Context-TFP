package nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import nl.tudelft.contextproject.ContextTFP;

import java.io.IOException;



public class CameraFeedsController {
    
    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        
    }
    
    
    /**
     * Calling this method shows this view in the middle of the rootLayout,
     * forcing the current view to disappear.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/CameramanLiveView.fxml"));
            AnchorPane cameraLiveUI = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(cameraLiveUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
