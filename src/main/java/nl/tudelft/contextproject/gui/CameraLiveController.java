package main.java.nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.java.nl.tudelft.contextproject.ContextTFP;
import main.java.nl.tudelft.contextproject.script.Script;

import java.io.IOException;

/**
 * This class controls the live view of the camera.
 * 
 * @since 0.3
 */
public class CameraLiveController {

    private static Script script;
    
    @FXML private Button btnBack;
    @FXML private Button swap;

    @FXML private ImageView bigView;
    @FXML private ImageView smallView;
    
    @FXML private TextArea smallDescriptionField;
    @FXML private TextArea bigDescriptionField;
    
    @FXML private VBox smallViewBox;
    @FXML private VBox bigViewBox;
    
    @FXML private Label bigStatusLabel;
    @FXML private Label smallStatusLabel;
    @FXML private Label smallShotNumberLabel;
    @FXML private Label smallCameraNumberLabel;
    @FXML private Label smallPresetLabel;
    @FXML private Label bigShotNumberLabel;
    @FXML private Label bigCameraNumberLabel;
    @FXML private Label bigPresetLabel;

    @FXML private void initialize() {
        script = ContextTFP.getScript();
        
        bigView.fitWidthProperty().bind(bigViewBox.widthProperty());
        bigView.fitHeightProperty().bind(bigViewBox.heightProperty());
        
        smallView.fitWidthProperty().bind(smallViewBox.widthProperty());
        smallView.fitHeightProperty().bind(smallViewBox.heightProperty());
        
        initializeLabels();
        initializeViews();
        initializeButtons();        
    }

    private void initializeLabels() {
        bigStatusLabel.setText("LIVE");
        bigStatusLabel.setStyle("-fx-text-fill: red;");
        smallStatusLabel.setText("Up next");
        
        if (script.getNextShot() != null) {
            smallShotNumberLabel.setText(script.getNextShot().getShotId());
            smallCameraNumberLabel.setText(Integer.toString(script.getNextShot().getCamera().getNumber()));
            smallPresetLabel.setText(Integer.toString(script.getNextShot().getPreset().getId()));
            smallDescriptionField.setText(script.getNextShot().getDescription());
        }
        
        if (script.getCurrentShot() != null) {
            bigShotNumberLabel.setText(script.getCurrentShot().getShotId());
            bigCameraNumberLabel.setText(Integer.toString(script.getCurrentShot().getCamera().getNumber()));
            bigPresetLabel.setText(Integer.toString(script.getCurrentShot().getPreset().getId()));
            bigDescriptionField.setText(script.getCurrentShot().getDescription());
        }
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
            String text = bigStatusLabel.getText();
            bigStatusLabel.setText(smallStatusLabel.getText());
            smallStatusLabel.setText(text);
            if (text.equals("LIVE")) {
                smallStatusLabel.setStyle("-fx-text-fill: red;");
                bigStatusLabel.setStyle("");
            } else {
                smallStatusLabel.setStyle("");
                bigStatusLabel.setStyle("-fx-text-fill: red;");
            }
        });
        
        btnBack.toFront();
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
