package main.java.nl.tudelft.contextproject.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
import java.util.HashMap;
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
        
        setFactories();
        tableView.getSortOrder().add(presetColumn);
        setActions();
    }

    private void setFactories() {
        presetColumn.setCellValueFactory(
                new PropertyValueFactory<Preset, Integer>("id"));

        descColumn.setCellValueFactory(
                new PropertyValueFactory<Preset, String>("description"));
    }

    private void setActions() {
        
//        SortedList<Preset> sortedList = new SortedList<>( data, 
//                (Preset p1, Preset p2) -> {
//                  if( p1.getId() < p2.getId() ) {
//                      return -1;
//                  } else if( p1.getId() > p2.getId() ) {
//                      return 1;
//                  } else {
//                      return 0;
//                  }
//              });
        tableView.setItems(data);

        btnBack.setOnAction((event) -> {
            MenuController.show();
        });

        //TODO: Save camera settings with the preset
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

        //TODO: Update current view of camera
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
                //TODO: Show error message
            }
        }
    }
    
    private void removePreset(int id) {
        int position = -1;
        
        for(int i = 0; i < data.size(); i++) {
            Preset p = data.get(i);
            if(p.getId() == id) {
                position = i;
                break;
            }
        }
        
        if(position != -1) {
            data.remove(position);
        }
    }
}
