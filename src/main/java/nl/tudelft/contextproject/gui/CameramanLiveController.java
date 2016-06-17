package nl.tudelft.contextproject.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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
import java.util.List;

/**
 * This class controls the screen that shows the view for a cameraman.
 * This screen allows a cameraman to see the script with the current 
 * active shot, and look at the {@link Preset Presets} of the 
 * camera.
 * 
 * <p>The view section is defined under view/CameramanLiveView.fxml
 * 
 * @since 0.8
 */
public class CameramanLiveController {

    private static List<CheckBox> cameras;
    
    @FXML private Button btnBack;
    @FXML private Button btnHideAll;
    @FXML private Button btnPresets;
    @FXML private Button btnNext;
    @FXML private Button btnShowAll;
    
    @FXML private TableView<Shot> tableShots;
    @FXML private TableColumn<Shot, Number> columnCamera;
    @FXML private TableColumn<Shot, String> columnDescription;
    @FXML private TableColumn<Shot, Number> columnID;
    @FXML private TableColumn<Shot, String> columnPreset;
    @FXML private TableColumn<Shot, String> columnShot;
    
    @FXML private VBox vbox;
    
    private Script script;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        script = ContextTFP.getScript();
        cameras = new ArrayList<CheckBox>();
        vbox.setSpacing(6);

        initCameraSelector();
        initButtons();
        initRowFactory();
        setFactories();

        tableShots.getItems().addAll(script.getShots());
        tableShots.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void initCameraSelector() {
        for (Camera c : Camera.getAllCameras()) {
            final CheckBox check = new CheckBox();
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
        
        //TODO remove temp button
        btnNext.setOnAction(event -> {
            ContextTFP.getScript().next();
            tableShots.refresh();
        });
        
        btnShowAll.setOnAction(event -> {
            toggleCameras(true);
        });
        
        btnHideAll.setOnAction(event -> {
            toggleCameras(false);
        });
        
        btnPresets.setOnAction(event -> {
            PresetController.setToCameramanView(true);
            
            Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                PresetController.show();
                Animation.animNodeIn(ContextTFP.getRootLayout());
            });
        });
    }
    
    /**
     * Toggles the cameras shown in the table.
     * @param show True if show, false if hide.
     */
    private void toggleCameras(boolean show) {
        for (CheckBox c : cameras) {
            c.setSelected(show);
        }
    }

    /**
     * Sub method of {@link #initButtons()} that fills a list
     * with all the shots that belong to the selected camera.
     */
    private List<Shot> fillListOfShots() {
        final List<Shot> listShots = new ArrayList<Shot>();
        
        for (Shot s : script.getShots()) {
            int shotCamNum = s.getCamera().getNumber();
            
            if (s.equals(script.getCurrentShot())
                    || cameras.get(shotCamNum).isSelected()) {
                listShots.add(s);
            }
        }
        
        return listShots;
    }
    
    /**
     * Sets the row factory of the table, enabling the 
     * indication of the current shot.
     */
    private void initRowFactory() {
        final PseudoClass currentPseudoClass = PseudoClass.getPseudoClass("current");

        tableShots.setRowFactory(table -> new TableRow<Shot>() {
            
            @Override
            protected void updateItem(Shot s, boolean b) {
                super.updateItem(s, b);
                if (s != null) {
                    boolean current = s.equals(script.getCurrentShot());
                    pseudoClassStateChanged(currentPseudoClass, current);
                    tableShots.refresh();
                }
            }
        });
    }

    /**
     * Sets the factories of the table columns.
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
