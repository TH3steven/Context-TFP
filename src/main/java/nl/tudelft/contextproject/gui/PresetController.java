package nl.tudelft.contextproject.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * This class is a control class for the preset creation screen. It
 * enables the creation and modification of presets for different cameras.
 * 
 * @since 0.2
 */
public class PresetController {

    @FXML private ChoiceBox<Integer> cameraSelecter;
    @FXML private TextField presetID;
    @FXML private TextField description;
    @FXML private ImageView cameraView;
    @FXML private Button btnBack;
    @FXML private Button btnSave;
    @FXML private Button btnRemove;
    @FXML private CheckBox overwrite;
    @FXML private TableView<Preset> tableView;
    @FXML private TableColumn<Preset, Integer> presetColumn;
    @FXML private TableColumn<Preset, String> descColumn;
    @FXML private VBox vBox;

    private ObservableList<Preset> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        List<Integer> cameraList = new ArrayList<Integer>();

        for (int i = 0; i < Camera.getCameraAmount(); i++) {
            cameraList.add(i + 1);
        }

        cameraSelecter.setItems(FXCollections.observableArrayList(cameraList));
        cameraView.setImage(new Image("placeholder_picture.jpg"));

        applySettings();
        setFactories();
        setActions();
        
        sort();
    }

    /**
     * Applies javafx settings that can't be specified in the fxml.
     */
    private void applySettings() {
        vBox.setAlignment(Pos.CENTER);
        cameraView.fitWidthProperty().bind(vBox.widthProperty());
        cameraView.fitHeightProperty().bind(vBox.heightProperty());
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
     * Adds all actions and listeners to the javafx components.
     */
    private void setActions() {
        tableView.setItems(data);

        btnBack.setOnAction((event) -> {
            MenuController.show();
        });

        //TODO: Save actual camera settings in the preset
        btnSave.setOnAction((event) -> {
            int id = -1;
            try {
                id = Integer.parseInt(presetID.getText());
                presetID.setStyle("");
                addPreset(id);
            } catch (Exception e) {
                presetID.setStyle("-fx-border-color: red;");
            }
        });

        //TODO: Update current view of camera with livefeed from camera
        cameraSelecter.setOnAction((event) -> {
            Camera cam = Camera.getCamera(cameraSelecter.getValue() - 1);
            HashMap<Integer, Preset> presets = cam.getPresets();
            data.clear();
            for (Preset p : presets.values()) {
                data.add(p);
            }
        });

        btnRemove.setOnAction((event) -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Preset selected = tableView.getItems().get(selectedIndex);
                Camera cam = Camera.getCamera(cameraSelecter.getValue() - 1);
                cam.removePreset(selected);
                data.remove(selected);
            }
        });
    }

    /**
     * Method for adding a preset to the view and the model.
     * @param id The id of the preset to add.
     */
    private void addPreset(int id) {
        Preset newPreset = new InstantPreset(
                new CameraSettings(1, 1, 1, 2000),
                id,
                description.getText());
        Camera cam = Camera.getCamera(cameraSelecter.getValue() - 1);

        if (overwrite.isSelected()) {
            cam.overwritePreset(newPreset);
            int newId = newPreset.getId();
            removePreset(newId);  
            addToTable(newPreset);
        } else {
            if (cam.addPreset(newPreset)) {
                addToTable(newPreset);
            } else {
                confirmOverwrite(newPreset, cam);
            }
        }
    }

    /**
     * Shows a message box to confirm overwriting a preset.
     * @param newPreset The preset which will overwrite another preset.
     * @param cam The camera of the preset that will be overridden.
     */
    private void confirmOverwrite(Preset newPreset, Camera cam) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm overwriting");
        alert.setHeaderText("You are about to overwrite a preset");
        alert.setContentText("Are you sure you want to overwrite preset " + newPreset.getId() + "?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            cam.overwritePreset(newPreset);
            int newId = newPreset.getId();
            removePreset(newId);  
            addToTable(newPreset);
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
     * Shows this view.
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
