package nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;

import java.io.IOException;

/**
 * This class controls the screen that shows the live view
 * of the {@link Camera} for the director. The director can use this screen
 * to use the application during live production to get an overview
 * of the live camera, a preview camera and the {@link Script}, along with controls
 * to control the cameras and the {@link Preset Presets} set to these cameras.
 * 
 * <p>The view section is defined under view/DirectorLiveView.fxml
 * 
 * @since 0.3
 */
public class DirectorLiveController {

    private static Script script;

    @FXML private Button btnBack;
    @FXML private Button btnSwap;

    @FXML private ImageView bigView;
    @FXML private ImageView smallView;

    @FXML private Label bigCameraNumberLabel;
    @FXML private Label bigShotNumberLabel;
    @FXML private Label bigPresetLabel;
    @FXML private Label bigStatusLabel;
    @FXML private Label smallCameraNumberLabel;
    @FXML private Label smallPresetLabel;
    @FXML private Label smallShotNumberLabel;
    @FXML private Label smallStatusLabel;

    @FXML private TextArea bigDescriptionField;
    @FXML private TextArea smallDescriptionField;

    @FXML private VBox bigViewBox;
    @FXML private VBox smallViewBox;

    /**
     * Initialize method used by JavaFX.
     */
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

    /**
     * Initializes the labels.
     */
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
     * Initializes the methods.
     */
    private void initializeButtons() {
        btnSwap.setOnAction((event) -> {
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
     * Calling this method shows this view in the middle of the rootLayout,
     * forcing the current view to disappear.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/DirectorLiveView.fxml"));
            AnchorPane cameraLiveUI = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(cameraLiveUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
