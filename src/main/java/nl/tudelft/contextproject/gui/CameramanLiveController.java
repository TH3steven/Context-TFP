package nl.tudelft.contextproject.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
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
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.MovementType;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;

import java.io.IOException;

/**
 * This class controls the screen that shows the live view of
 * the {@link Camera}, for the cameraman. This screen allows a cameraman to see the
 * current live view, look at the {@link Preset Presets} of the camera, and apply
 * both new and pre-configured presets.
 * 
 * <p>The view section is defined under view/CameramanLiveView.fxml
 * 
 * @since 0.3
 */
public class CameramanLiveController {

    private static Script script;

    @FXML private TabPane tabs;

    @FXML private Button btnBack;
    @FXML private Button btnConfirm;
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

    @FXML private Label bigStatusLabel;
    @FXML private Label smallStatusLabel;
    @FXML private Label cameraNumberLabel;
    @FXML private Label presetLabel;
    @FXML private Label movement;
    @FXML private Label infoLabel;

    @FXML private TextArea descriptionField;
    @FXML private TextField speed;

    @FXML private VBox bigViewBox;
    @FXML private VBox smallViewBox;


    private int currentView;

    private Camera liveCam;
    private Camera currentCam;

    private int liveSpeed;
    private int currentSpeed;

    private MovementType liveMovement;
    private MovementType currentMovement;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        script = ContextTFP.getScript();

        bigView.fitWidthProperty().bind(bigViewBox.widthProperty());
        bigView.fitHeightProperty().bind(bigViewBox.heightProperty());

        smallView.fitWidthProperty().bind(smallViewBox.widthProperty());
        smallView.fitHeightProperty().bind(smallViewBox.heightProperty());

        currentView = 0;

        //Initialize dummy cameras and presets
        // TODO connect to actual backend implementation.
        liveCam = new Camera();
        currentCam = new Camera();

        CameraSettings dummySettings = new CameraSettings();

        Preset presetOne = new InstantPreset(dummySettings, 1);
        Preset presetTwo = new InstantPreset(dummySettings, 2);

        liveCam.addPreset(presetOne);
        currentCam.addPreset(presetTwo);
        //

        initializeLabels();
        initializeViews();
        initializeButtons();
        initializeMovements();
        initializeTextFields();
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
        initSwapButton();

        btnBack.setOnAction(event -> {
            MenuController.show();
        });

        btnConfirm.setOnAction(event -> {
            if (!speed.getText().isEmpty()) {
                int spd = Integer.parseInt(speed.getText());

                if (spd > 100) {
                    spd = 100;
                }

                if (live()) {                   
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
     * Initializes the swap button.
     */
    private void initSwapButton() {
        swap.setOnAction(event -> {
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
    }

    /**
     * Initializes movements.
     */
    private void initializeMovements() {
        moveOne.setOnAction(event -> {
            movement.setText(moveOne.getText());
            setMovement(MovementType.ZOOM_IN);
        });

        moveTwo.setOnAction(event -> {
            movement.setText(moveTwo.getText());
            setMovement(MovementType.ZOOM_OUT);
        });

        moveThree.setOnAction(event -> {
            movement.setText(moveThree.getText());
            setMovement(MovementType.LEFT);
        });

        moveFour.setOnAction(event -> {
            movement.setText(moveFour.getText());
            setMovement(MovementType.RIGHT);
        });

        moveFive.setOnAction(event -> {
            movement.setText(moveFive.getText());
            setMovement(MovementType.UP);
        });

        moveSix.setOnAction(event -> {
            movement.setText(moveSix.getText());
            setMovement(MovementType.DOWN);
        });

        moveSeven.setOnAction(event -> {
            movement.setText(moveSeven.getText());
            setMovement(MovementType.CUSTOM);
        });
    }

    /**
     * Initializes the textfields.
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
     * Swaps the currentView, depending on the previous state
     * of currentView. 0 becomes 1 and 1 becomes 0.
     */
    private void swapCurrentView() {
        currentView = (live()) ? 1 : 0;
    }

    /**
     * Swap info table according to the big view.
     */
    private void swapInfoTable() {
        Preset preset;

        if (live()) {
            preset = liveCam.getPreset(1);
            infoLabel.setText("Information about live camera");
            cameraNumberLabel.setText(String.valueOf(liveCam.getNumber()));
            movement.setText(MovementType.getName(liveMovement));
            descriptionField.setText(preset.getDescription());
        } else {
            preset = currentCam.getPreset(2);
            infoLabel.setText("Information about current camera");
            cameraNumberLabel.setText(String.valueOf(currentCam.getNumber()));
            movement.setText(MovementType.getName(currentMovement));

        }

        presetLabel.setText(String.valueOf(preset.getId()));
        descriptionField.setText(preset.getDescription());
    }

    /**
     * Swap speed according to the big view.
     */
    private void swapSpeed() {
        if (live()) {
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
     * Set movement type to the big view.
     * @param mt The movement type to set.
     */
    private void setMovement(MovementType mt) {
        if (live()) {
            liveMovement = mt;
        } else {
            currentMovement = mt;
        }
    }

    /**
     * See what is currently on the big view.
     * @return Whether it's the live camera on the view or the current camera.
     */
    private boolean live() {
        return (currentView == 0) ? true : false;
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
