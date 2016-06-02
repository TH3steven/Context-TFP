package nl.tudelft.contextproject.gui;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.MovementType;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;

import java.io.IOException;

/**
 * This class controls the live view of the camera.
 * 
 * @since 0.3
 */
public class CameramanUIController {

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
    @FXML private Button btnConfirm;
    
    @FXML private ChoiceBox<Camera> currentCam;

    @FXML private ImageView bigView;
    @FXML private ImageView smallView;
    
    @FXML private TextArea descriptionField;
    
    @FXML private TextField speed;
    
    @FXML private VBox smallViewBox;
    @FXML private VBox bigViewBox;
    
    @FXML private Label bigStatusLabel;
    @FXML private Label smallStatusLabel;
    @FXML private Label cameraNumberLabel;
    @FXML private Label presetLabel;
    @FXML private Label movement;
    @FXML private Label infoLabel;
    
    private boolean live;
    
    private Camera liveCam;
    
    private int liveSpeed;
    private int currentSpeed;
    
    private MovementType liveMovement;
    private MovementType currentMovement;

    @FXML private void initialize() {
        script = ContextTFP.getScript();
        
        //set an initial live camera
        liveCam = Camera.getCamera(0);
        
        bigView.fitWidthProperty().bind(bigViewBox.widthProperty());
        bigView.fitHeightProperty().bind(bigViewBox.heightProperty());
        
        smallView.fitWidthProperty().bind(smallViewBox.widthProperty());
        smallView.fitHeightProperty().bind(smallViewBox.heightProperty());
        
        live = true;
        
        initializeLabels();
        initializeViews();
        initializeButtons();
        initializeMovements();
        initializeTextFields();
        initializeCurrentCam();
        
        descriptionField.setEditable(false);
    }
    
    /**
     * Initializes the labels in the gui.
     */
    private void initializeLabels() {
        bigStatusLabel.setText("LIVE");
        bigStatusLabel.setStyle("-fx-text-fill: red;");
        smallStatusLabel.setText("Current camera");
        
        if (script.getCurrentShot() != null) {
            cameraNumberLabel.setText(Integer.toString(script.getCurrentShot().getCamera().getNumber()));
            presetLabel.setText(Integer.toString(script.getCurrentShot().getPreset().getId()));
            descriptionField.setText(script.getCurrentShot().getDescription());
        }
        cameraNumberLabel.setText(String.valueOf(liveCam.getNumber()));
        presetLabel.setText(String.valueOf(liveCam.getPreset(1).getId()));
    }
    
    /**
     * Initializes the views.
     */
    private void initializeViews() {
        Image actual = new Image("placeholder_picture.jpg");
        bigView.setImage(actual);

        Image current = new Image("test3.jpg");
        smallView.setImage(current);
    }
    
    /**
     * Initialize button styling and functionality.
     */
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
            swapInfoTable();
            swapSpeed();
        });
        
        btnBack.setOnAction((event) -> {
            MenuController.show();
        });
        
        btnConfirm.setOnAction((event) -> {
            if (!speed.getText().isEmpty()) {
                int spd = Integer.parseInt(speed.getText());
                if (spd > 100) {
                    spd = 100;
                }
                if (live) {                   
                    liveSpeed = spd;
                    speed.setText(String.valueOf(spd));
                } else {
                    currentSpeed = spd;
                    speed.setText(String.valueOf(spd));
                }
            }
        });
    }
    
    /**
     * Initialize movements.
     */
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
    
    /**
     * Initialize the textfields.
     */
    private void initializeTextFields() {
        speed.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String character = event.getCharacter();

                if (!character.matches("[0-9]")) {
                    event.consume();
                } else if (speed.getText().length() == 3) {
                    event.consume();
                }
            }
        });
    }
    
    /**
     * Initializes the choicebox of cameras to choose from.
     */
    private void initializeCurrentCam() {
        currentCam.setItems(FXCollections.observableArrayList(Camera.getAllCameras()));
        currentCam.setVisible(false);
        
        currentCam.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> { 
            changeInfoTable(newV);
        });
        
        //set initial current camera
        currentCam.setValue(Camera.getCamera(1));
    }
    
    /**
     * Swap currentView.
     */
    private void swapCurrentView() {
        if (live) {
            live = false;
            currentCam.setVisible(true);
        } else {
            live = true;
            currentCam.setVisible(false);
        }
    }
    
    /**
     * Swap info table according to the big view.
     */
    private void swapInfoTable() {
        if (live) {
            setActualInfo();
        } else {
            setCurrentInfo();
        }
    }
    
    /**
     * Changes te info table according to the currently selected camera.
     * @param newCam The new camera to switch the info to.
     */
    private void changeInfoTable(Camera newCam) {
        Preset preset;
        if (newCam.getPreset(1) == null) {
            preset = newCam.getPreset(2);
        } else {
            preset = newCam.getPreset(1);
        }
        infoLabel.setText("Information about current camera");
        cameraNumberLabel.setText(String.valueOf(newCam.getNumber()));
        presetLabel.setText(String.valueOf(preset.getId()));
        movement.setText(MovementType.getName(currentMovement));
        descriptionField.setText(preset.getDescription());
    }
    
    /**
     * Swap speed according to the big view.
     */
    private void swapSpeed() {
        if (live) {
            if (liveSpeed > 0) {
                speed.setText(String.valueOf(liveSpeed));
            } else {
                speed.setText("");
            }
        } else {
            if (currentSpeed > 0) {
                speed.setText(String.valueOf(currentSpeed));
            } else {
                speed.setText("");
            }
        }
    }
    
    /**
     * set movement type to the big view.
     * @param mt - the movement type to set.
     */
    private void setMovement(MovementType mt) {
        if (live) {
            liveMovement = mt;
        } else {
            currentMovement = mt;
        }
    }
    
    /**
     * Set the table info to the actual camera info.
     */
    private void setActualInfo() {
        Preset preset = liveCam.getPreset(1);
        infoLabel.setText("Information about live camera");
        cameraNumberLabel.setText(String.valueOf(liveCam.getNumber()));
        presetLabel.setText(String.valueOf(preset.getId()));
        movement.setText(MovementType.getName(liveMovement));
        descriptionField.setText(preset.getDescription());
    }
    
    /**
     * Set the table info to the current camera info.
     */
    private void setCurrentInfo() {
        Preset preset = currentCam.getValue().getPreset(2);
        infoLabel.setText("Information about current camera");
        cameraNumberLabel.setText(String.valueOf(currentCam.getValue().getNumber()));
        presetLabel.setText(String.valueOf(preset.getId()));
        movement.setText(MovementType.getName(currentMovement));
        descriptionField.setText(preset.getDescription());
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/CameramanUIView.fxml"));
            AnchorPane cameraLiveUI = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(cameraLiveUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
