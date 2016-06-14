package nl.tudelft.contextproject.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;



public class CameraFeedsController {
    @FXML private ChoiceBox<Camera> camChoiceOne;
    @FXML private ChoiceBox<Camera> camChoiceTwo;
    
    @FXML private ImageView tempOne;
    @FXML private ImageView tempTwo;
    
    private Collection<Camera> cameras;
    
    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        cameras = Camera.getAllCameras();
        
        initImages();
        initChoiceBoxes();
    }
    
    /**
     * Initializes temporary imageviews.
     * These will get replaces by camera live feeds.
     */
    private void initImages() {
        tempOne.setImage(new Image("black.png"));
        tempTwo.setImage(new Image("black.png"));
    }
    
    /**
     * Initializes the choice boxes.
     * Allows the user to choose his camera feed for each view.
     */
    private void initChoiceBoxes() {
        ObservableList<Camera> choices = FXCollections.observableArrayList();
        Iterator<Camera> it = cameras.iterator();
        while (it.hasNext()) {
            choices.add(it.next());
        }

        camChoiceOne.setItems(choices);
        camChoiceTwo.setItems(choices);
    }
    
    /**
     * Loads an image. Loads an error image if path is null or invalid.
     * 
     * @param path Image path.
     * @return The newly loaded image.
     */
    private Image loadImage(String path) {
        try {
            return new Image(path);
        } catch (IllegalArgumentException e) {
            return new Image("error.jpg");
        }
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
