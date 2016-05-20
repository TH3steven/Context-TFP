package main.java.nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import main.java.nl.tudelft.contextproject.ContextTFP;
import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.presets.MovementType;
import main.java.nl.tudelft.contextproject.script.Script;

import java.io.IOException;

/**
 * This class controls the live view of the camera.
 * 
 * @since 0.3
 */
public class CameramenUIController {

    private static Script script;
    
    @FXML private TabPane tabs;
    
    @FXML private Button btnBack;
    @FXML private Button swap;
    @FXML private Button moveOne;
    @FXML private Button moveTwo;
    @FXML private Button moveThree;
    @FXML private Button moveFour;
    @FXML private Button moveFive;
    @FXML private Button moveSix;
    @FXML private Button moveSeven;

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
    @FXML private Label movement;
    
    private int currentView;
    
    private Camera liveCam;
    private Camera currentCam;
    
    private MovementType liveMovement;
    private MovementType currentMovement;

    @FXML private void initialize() {
        script = ContextTFP.getScript();
        
        bigView.fitWidthProperty().bind(bigViewBox.widthProperty());
        bigView.fitHeightProperty().bind(bigViewBox.heightProperty());
        
        smallView.fitWidthProperty().bind(smallViewBox.widthProperty());
        smallView.fitHeightProperty().bind(smallViewBox.heightProperty());
        
        currentView = 0;
        
        //Initialize dummy cameras
        liveCam = new Camera();
        currentCam = new Camera();
        
        initializeLabels();
        initializeViews();
        initializeButtons();
        initializeMovements();
    }

    private void initializeLabels() {
        bigStatusLabel.setText("LIVE");
        bigStatusLabel.setStyle("-fx-text-fill: red;");
        smallStatusLabel.setText("Current camera");
        
        if (script.getNextShot() != null) {
            smallShotNumberLabel.setText(script.getNextShot().getShotId());;
            smallCameraNumberLabel.setText(Integer.toString(script.getNextShot().getCamera().getNumber()));
            smallPresetLabel.setText(Integer.toString(script.getNextShot().getPreset().getId()));
            smallDescriptionField.setText(script.getNextShot().getDescription());
        }
        
        if (script.getCurrentShot() != null) {
            bigShotNumberLabel.setText(script.getCurrentShot().getShotId());;
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
            swapCurrentView();
        });
        
        btnBack.setOnAction((event) -> {
            MenuController.show();
        });
    }
    
    private void initializeMovements() {
        moveOne.setOnAction((event) -> {
            movement.setText(moveOne.getText());
            setMovement(MovementType.ZOOM_IN);
        });
        
        moveTwo.setOnAction((event) -> {
            movement.setText(moveTwo.getText());
            setMovement(MovementType.ZOOM_OUT);
        });
        
        moveThree.setOnAction((event) -> {
            movement.setText(moveThree.getText());
            setMovement(MovementType.LEFT);
        });
        
        moveFour.setOnAction((event) -> {
            movement.setText(moveFour.getText());
            setMovement(MovementType.RIGHT);
        });
        
        moveFive.setOnAction((event) -> {
            movement.setText(moveFive.getText());
            setMovement(MovementType.UP);
        });
        
        moveSix.setOnAction((event) -> {
            movement.setText(moveSix.getText());
            setMovement(MovementType.DOWN);
        });
        
        moveSeven.setOnAction((event) -> {
            movement.setText(moveSeven.getText());
            setMovement(MovementType.CUSTOM);
        });
    }
    
    private void swapCurrentView() {
        if (currentView == 0) {
            currentView = 1;
        } else {
            currentView = 0;
        }
    }
    
    private void setMovement(MovementType mt) {
        if (currentView == 0) {
            liveMovement = mt;
        } else {
            currentMovement = mt;
        }
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/CameramenUIView.fxml"));
            AnchorPane cameraLiveUI = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(cameraLiveUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
