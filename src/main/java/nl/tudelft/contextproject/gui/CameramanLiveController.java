package nl.tudelft.contextproject.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
    
    @FXML private TableView<Shot> tableShots;
    @FXML private TableColumn<Shot, Number> columnCamera;
    @FXML private TableColumn<Shot, String> columnDescription;
    @FXML private TableColumn<Shot, Number> columnID;
    @FXML private TableColumn<Shot, String> columnPreset;
    @FXML private TableColumn<Shot, String> columnShot;
    
    @FXML private VBox vbox;

    private List<CheckBox> cameras;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        script = ContextTFP.getScript();
        cameras = new ArrayList<CheckBox>();
        vbox.setSpacing(6);

        initCameraSelector();
        initButtons();
        initShotListener();
        setFactories();

        tableShots.getItems().addAll(script.getShots());
        tableShots.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void initCameraSelector() {
        for (Camera c : Camera.getAllCameras()) {
            CheckBox check = new CheckBox();
            check.setText("Camera: " + (c.getNumber() + 1));
            check.setContentDisplay(ContentDisplay.LEFT);
            check.fire();
            cameras.add(check);

            check.selectedProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    List<Shot> listShots = fillListOfShots();
                    tableShots.getItems().setAll(listShots);
                }
            });

            vbox.getChildren().add(check);
        }
    }

    /**
     * Initialize button functionality.
     */
    private void initButtons() {
        btnBack.setOnAction(event -> {
            MenuController.show();
        });
    }

    /**
     * Submethod of {@link #initButtons()} that fills a list
     * with all the shots that belong to the selected camera.
     */
    private List<Shot> fillListOfShots() {
        List<Shot> listShots = new ArrayList<Shot>();
        
        for (Shot s : script.getShots()) {
            int shotCamNum = s.getCamera().getNumber();
            
            if (cameras.get(shotCamNum).isSelected()) {
                listShots.add(s);
            }
        }

        return listShots;
    }

    private void initShotListener() {
        //
    }

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
