package nl.tudelft.contextproject.gui;

import javafx.beans.property.FloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is a control class for the preset creation screen. It
 * enables the creation and modification of presets for different cameras.
 * 
 * @since 0.2
 */
public class PresetController {

    private static boolean toCameramanView = false;

    @FXML private CheckBox overwrite;
    @FXML private ChoiceBox<Integer> cameraSelecter;

    @FXML private Button btnBack;
    @FXML private Button btnSave;
    @FXML private Button btnRemove;

    @FXML private TableView<Preset> tableView;
    @FXML private TableColumn<Preset, Integer> presetColumn;
    @FXML private TableColumn<Preset, String> descColumn;

    @FXML private TextField presetID;
    @FXML private TextField description;

    @FXML private VBox vBox;

    private LiveStreamHandler streamHandler;
    private ObservableList<Preset> data = FXCollections.observableArrayList();
    private ImageView imageView;

    /**
     * Initialize method for JavaFX.
     */
    @FXML private void initialize() {
        List<Integer> cameraList = new ArrayList<Integer>();

        for (int i = 0; i < Camera.getCameraAmount(); i++) {
            cameraList.add(i + 1);
        }

        cameraSelecter.setItems(FXCollections.observableArrayList(cameraList));

        applySettings();
        setFactories();
        setActions();

        sort();
    }

    /**
     * Applies JavaFX settings that can't be specified in the FXML.
     */
    private void applySettings() {
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().clear();
    }

    /**
     * Updates the camera stream to the stream referenced by the specified link.
     * @param streamLink the link to the video stream to be played next.
     */
    private void updateStream(String streamLink) {
        if (streamHandler != null) {
            streamHandler.stop();
        }

        streamHandler = new LiveStreamHandler();
        imageView = streamHandler.createImageView(streamLink, 1920, 1080);
        new Thread(() -> {
            fitImageViewSize((float) vBox.getWidth(), (float) vBox.getHeight());
        }).start();

        vBox.getChildren().clear();
        vBox.getChildren().add(imageView);
        streamHandler.start();
    }

    /**
     * Sets the factories for the table.
     */
    private void setFactories() {
        presetColumn.setCellValueFactory(
            new PropertyValueFactory<Preset, Integer>("id"));

        descColumn.setCellValueFactory(
            new PropertyValueFactory<Preset, String>("description"));
    }

    /**
     * Adds all actions and listeners to the JavaFX components.
     */
    private void setActions() {
        tableView.setItems(data);

        btnRemove.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Preset selected = tableView.getItems().get(selectedIndex);
                Camera cam = Camera.getCamera(cameraSelecter.getValue() - 1);
                cam.removePreset(selected);
                data.remove(selected);
            }
        });

        vBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (streamHandler != null) {
                fitImageViewSize(newValue.floatValue(), (float) vBox.getHeight());
            }
        });

        vBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (streamHandler != null) {
                fitImageViewSize((float) vBox.getWidth(), newValue.floatValue());
            }
        });

        setSaveButton();
        setBackButton();
        setCameraSelector();
    }

    /**
     * Sets the onAction for the save button.
     */
    private void setSaveButton() {
        btnSave.setOnAction(event -> {
            int id = -1;

            try {
                id = Integer.parseInt(presetID.getText());
                presetID.setStyle("");
                addPreset(id);
            } catch (Exception e) {
                presetID.setStyle("-fx-border-color: red;");
            }
        });
    }

    /**
     * Sets the onAction for the back button.
     */
    private void setBackButton() {
        btnBack.setOnAction(event -> {
            if (streamHandler != null) {
                streamHandler.stop();
            }

            if (toCameramanView) {
                Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                    CameramanLiveController.show();
                    Animation.animNodeIn(ContextTFP.getRootLayout());
                });
            } else {
                MenuController.show();
            }
        });
    }

    /**
     * Sets the onAction for the camera selection choicebox.
     */
    private void setCameraSelector() {
        cameraSelecter.setOnAction(event -> {
            Camera cam = Camera.getCamera(cameraSelecter.getValue() - 1);
            HashMap<Integer, Preset> presets = cam.getPresets();
            data.clear();
            for (Preset p : presets.values()) {
                data.add(p);
            }
            if (cam.hasConnection()) {
                updateStream(cam.getConnection().getStreamLink());
            } else {
                updateStream("http://www.formisimo.com/blog/wp-content/uploads/2014/04/error-mesage.png");
            }
        });
    }

    /**
     * Resizes the ImageView.
     * 
     * @param width The new width of the ImageView.
     * @param height The new height of the ImageView.
     */
    private void fitImageViewSize(float width, float height) {
        FloatProperty videoSourceRatioProperty = streamHandler.getRatio();
        float fitHeight = videoSourceRatioProperty.get() * width;

        if (fitHeight > height) {
            imageView.setFitHeight(height);
            double fitWidth = height / videoSourceRatioProperty.get();
            imageView.setFitWidth(fitWidth);
            imageView.setX((width - fitWidth) / 2);
            imageView.setY(0);
        } else {
            imageView.setFitWidth(width);
            imageView.setFitHeight(fitHeight);
            imageView.setY((height - fitHeight) / 2);
            imageView.setX(0);
        }   
    }

    /**
     * Method for adding a preset to the view and the model.
     * @param id The id of the preset to add.
     */
    private void addPreset(int id) {
        Camera cam = Camera.getCamera(cameraSelecter.getValue() - 1);
        Preset newPreset = new InstantPreset(
                cam.getSettings(),
                id,
                description.getText());

        if (overwrite.isSelected()) {
            cam.overwritePreset(newPreset);
            int newId = newPreset.getId();
            removePreset(newId);  
            addToTable(newPreset);
        } else {
            if (cam.addPreset(newPreset)) {
                addToTable(newPreset);
            } else {
                if (AlertDialog.confirmPresetOverwrite(newPreset, cam)) {
                    cam.overwritePreset(newPreset);
                    int newId = newPreset.getId();
                    removePreset(newId);  
                    addToTable(newPreset);
                }
            }
        }
    }

    /**
     * Used to add a preset to the table view.
     * @param p The preset to add.
     */
    private void addToTable(Preset p) {
        data.add(p);
        sort();
    }

    /**
     * Sorts the tableview, ascending to preset id.
     */
    private void sort() {
        tableView.getSortOrder().clear();
        tableView.getSortOrder().add(presetColumn);
    }

    /**
     * Because the preset id might not be the same as the position in the list, 
     * this method is required to remove a preset.
     * @param id The id of the preset to remove.
     */
    private void removePreset(int id) {
        for (int i = 0; i < data.size(); i++) {
            Preset p = data.get(i);
            if (p.getId() == id) {
                data.remove(i);
                break;
            }
        }
    }

    /**
     * Sets the toCameramanView, that sets the back button to
     * go back to the cameraman view if the user came from there.
     * @param bSet True iff the back button should go to the
     *      CameramanLiveView.
     */
    public static void setToCameramanView(boolean bSet) {
        toCameramanView = bSet;
    }

    /**
     * Calling this method shows this view in the middle of the rootLayout,
     * forcing the current view to disappear.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/PresetView.fxml"));
            AnchorPane createScriptView = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(createScriptView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
