package main.java.nl.tudelft.contextproject.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import main.java.nl.tudelft.contextproject.ContextTFP;

import java.io.IOException;

/**
 * Controller class for the script creation screen.
 * 
 * @author Tim Yue
 */
public class PreviewController {
    @FXML HBox shotBox;
    @FXML ImageView actualCam;
    @FXML ImageView imgOne;
    @FXML ImageView imgTwo;
    @FXML ImageView imgThree;
    @FXML ImageView imgFour;

    @FXML
    private void initialize() {
        actualCam.setImage(new Image("main/resources/placeholder_picture.jpg"));
        imgOne.setImage(new Image("main/resources/placeholder_picture.jpg"));
        imgTwo.setImage(new Image("main/resources/placeholder_picture.jpg"));
        imgThree.setImage(new Image("main/resources/placeholder_picture.jpg"));
        imgFour.setImage(new Image("main/resources/placeholder_picture.jpg"));
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/ScriptPreview.fxml"));
            AnchorPane createScriptView = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(createScriptView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
