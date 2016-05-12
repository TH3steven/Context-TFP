package main.java.nl.tudelft.contextproject.gui;

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
import main.java.nl.tudelft.contextproject.ContextTFP;
import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.presets.Preset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PresetController {

    @FXML private ChoiceBox<Integer> cameraSelecter;
    @FXML private TextField presetID;
    @FXML private TextField description;
    @FXML private ImageView cameraView;
    @FXML private Button btnBack;
    @FXML private Button btnSave;
    @FXML private CheckBox overwrite;
    @FXML private TableView<Preset> tableView;
    @FXML private TableColumn<Preset, Integer> presetColumn;
    @FXML private TableColumn<Preset, String> descColumn;
    @FXML private VBox vBox;
    private ObservableList<Preset> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

        //TEMP
        Camera c = new Camera();
        Camera c2 = new Camera();
        c.addPreset(new InstantPreset(new CameraSettings(), 1, "wow"));
        c.addPreset(new InstantPreset(new CameraSettings(), 5, "nice"));
        c.addPreset(new InstantPreset(new CameraSettings(), 3, "awesome"));
        c2.addPreset(new InstantPreset(new CameraSettings(), 1, "huh"));
        //

        List<Integer> cameraList = new ArrayList<Integer>();
        for (int i = 0; i < Camera.getCameraAmount(); i++) {
            cameraList.add(i + 1);
        }
        cameraSelecter.setItems(FXCollections.observableArrayList(cameraList));
        cameraView.setImage(new Image("main/resources/placeholder_picture.jpg"));
        
        applySettings();
        setFactories();
        setActions();
    }

    private void applySettings() {
        vBox.setAlignment(Pos.CENTER);
        cameraView.fitWidthProperty().bind(vBox.widthProperty());
        cameraView.fitHeightProperty().bind(vBox.heightProperty());
    }
    
    private void setFactories() {
        presetColumn.setCellValueFactory(
                new PropertyValueFactory<Preset, Integer>("id"));

        descColumn.setCellValueFactory(
                new PropertyValueFactory<Preset, String>("description"));
    }

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
            data.add(newPreset);
            tableView.getSortOrder().clear();
            tableView.getSortOrder().add(presetColumn);
        } else {
            if (cam.addPreset(newPreset)) {
                data.add(newPreset);
                tableView.getSortOrder().clear();
                tableView.getSortOrder().add(presetColumn);
            } else {
                confirmOverwrite(newPreset, cam);
            }
        }
    }
    
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
            data.add(newPreset);
            tableView.getSortOrder().clear();
            tableView.getSortOrder().add(presetColumn);
        }
    }
    
    private void removePreset(int id) {
        int position = -1;
        
        for (int i = 0; i < data.size(); i++) {
            Preset p = data.get(i);
            if (p.getId() == id) {
                position = i;
                break;
            }
        }
        
        if (position != -1) {
            data.remove(position);
        }
    }
}
