package main.java.nl.tudelft.contextproject.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import main.java.nl.tudelft.contextproject.ContextTFP;
import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.presets.Preset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    @FXML
    private void initialize() {

        //TEMP
        Camera c = new Camera();
        c.addPreset(new InstantPreset(new CameraSettings(), 0));
        c.addPreset(new InstantPreset(new CameraSettings(), 1));
        c.addPreset(new InstantPreset(new CameraSettings(), 2));
        //
        
        List<Integer> cameraList = new ArrayList<Integer>();
        for (int i = 0; i < Camera.getCameraAmount(); i++) {
            cameraList.add(i + 1);
        }
        cameraSelecter.setItems(FXCollections.observableArrayList(cameraList));
        cameraView.setImage(new Image("main/resources/placeholder_picture.jpg"));

        setFactories();
        setActions();
    }
    
    private void setFactories() {
        presetColumn.setCellValueFactory(
                new PropertyValueFactory<Preset, Integer>("id"));
        
        descColumn.setCellValueFactory(
                new PropertyValueFactory<Preset, String>("description"));
    }
    
    private void setActions() {
        final ObservableList<Preset> data = FXCollections.observableArrayList();
        tableView.setItems(data);
        
        btnBack.setOnAction((event) -> {
                MenuController.show();
            });
        
        //TODO: Add newly created preset to camera.
        btnSave.setOnAction((event) -> {
            int id = -1;
            try {
                id = Integer.parseInt(presetID.getText());
                presetID.setStyle("");
                data.add(new InstantPreset(
                        new CameraSettings(1, 1, 1, 2000),
                        id,
                        description.getText()));
            } catch (Exception e) {
                presetID.setStyle("-fx-border-color: red;");
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
}
