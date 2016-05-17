package main.java.nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import main.java.nl.tudelft.contextproject.ContextTFP;

import java.io.IOException;

/**
 * This class controls the live view of the camera.
 * 
 * @since 0.3
 */
public class CameraLiveController {

    @FXML private Button btnBack;
    @FXML private Button swap;

    @FXML private ImageView bigView;
    @FXML private ImageView smallView;
    
    @FXML private Label bigLabel;
    @FXML private Label smallLabel;

    @FXML private void initialize() {
        initializeLabels();
        initializeViews();
        initializeButtons();
    }

    private void initializeLabels() {
        bigLabel.setText("LIVE");
        bigLabel.setStyle("-fx-text-fill: red;");
        smallLabel.setText("Up next");
    }
    
    private void initializeViews() {
        Image actual = new Image("main/resources/placeholder_picture.jpg");
        bigView.setImage(actual);

        Image current = new Image("main/resources/test3.jpg");
        smallView.setImage(current);
    }

    private void initializeButtons() {
        swap.setOnAction((event) -> {
            Image three = bigView.getImage();
            bigView.setImage(smallView.getImage());
            smallView.setImage(three);
            String text = bigLabel.getText();
            bigLabel.setText(smallLabel.getText());
            smallLabel.setText(text);
            if (text.equals("LIVE")) {
                smallLabel.setStyle("-fx-text-fill: red;");
                bigLabel.setStyle("");
            } else {
                smallLabel.setStyle("");
                bigLabel.setStyle("-fx-text-fill: red;");
            }
        });
        
        btnBack.setOnAction((event) -> {
            MenuController.show();
        });
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/CameraLiveView.fxml"));
            AnchorPane cameraLiveUI = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(cameraLiveUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
