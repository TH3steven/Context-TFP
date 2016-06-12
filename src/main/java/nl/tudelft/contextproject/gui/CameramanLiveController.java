package nl.tudelft.contextproject.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class controls the screen that shows the live view of
 * the {@link Camera}, for the cameraman. This screen allows a cameraman to see the
 * script with the current active shot, look at the {@link Preset Presets} of the 
 * camera, and set the camera in the position of the next preset.
 * 
 * <p>The view section is defined under view/CameramanLiveView.fxml
 * 
 * @since 0.8
 */
public class CameramanLiveController {

    private Script script;

    @FXML private Button btnBack;
    @FXML private Button btnNextPreset;
    @FXML private ChoiceBox<String> cbCameras;

    @FXML private TableView<Shot> tableShots;
    @FXML private TableColumn<Shot, Number> columnCamera;
    @FXML private TableColumn<Shot, String> columnDescription;
    @FXML private TableColumn<Shot, Number> columnID;
    @FXML private TableColumn<Shot, String> columnPreset;
    @FXML private TableColumn<Shot, String> columnShot;
    
    @FXML private Camera camera;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        script = ContextTFP.getScript();
        
        initButtons();
        initCamSelector();
        initShotListener();
        setFactories();
        
        tableShots.getItems().addAll(script.getShots());
        tableShots.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Initialize button functionality.
     */
    private void initButtons() {
        btnBack.setOnAction(event -> {
            MenuController.show();
        });
        
        btnNextPreset.setOnAction(event -> {
            script.getNextShot().getPreset().applyTo(camera);
        });
    }
    
    private void initCamSelector() {
        List<String> list = new ArrayList<String>();
        list.add("Show all cameras");
        
        for (Camera c : Camera.getAllCameras()) {
            list.add("Camera: " + (c.getNumber() + 1));
        }
        
        cbCameras.setItems(FXCollections.observableArrayList(list));
        cbCameras.getSelectionModel().selectFirst();
        cbCameras.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                List<Shot> listShots = fillListOfShots(newV);
                tableShots.getItems().setAll(listShots);
            }
        });
    }
    
    /**
     * Submethod of {@link #initButtons()} that fills a list
     * with all the shots that belong to the selected camera.
     */
    private List<Shot> fillListOfShots(String newV) {
        List<Shot> listShots = new ArrayList<Shot>();
        String trimmed = newV.substring(newV.length() - 1);
        boolean set = true;
        
        for (Shot s : script.getShots()) {
            if (newV == cbCameras.getItems().get(0)) {
                listShots.add(s);
                if (set) {
                    camera = null;
                    btnNextPreset.setDisable(true);
                    set = false;
                }
            } else if (Integer.toString(s.getCamera().getNumber() + 1).equals(trimmed)) {
                listShots.add(s);
                if (set) {
                    camera = s.getCamera();
                    btnNextPreset.setDisable(false);
                    set = false;
                }
            }
        }
        
        return listShots;
    }
    
    private void initShotListener() {
        //
    }
    
    /**
     * Sets the factories for the tableView.
     */
    private void setFactories() {
        columnID.setCellValueFactory(new PropertyValueFactory<Shot, Number>("number"));
        
        columnShot.setCellValueFactory(new PropertyValueFactory<Shot, String>("shotId"));
        
        columnPreset.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPreset() == null) {
                return new ReadOnlyObjectWrapper<>();
            } else {
                return new ReadOnlyObjectWrapper<>(
                        Integer.toString(cellData.getValue().getPreset().getId() + 1));
            }
        });
        
        columnDescription.setCellValueFactory(new PropertyValueFactory<Shot, String>("description"));
        
        columnCamera.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getCamera().getNumber() + 1));
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
