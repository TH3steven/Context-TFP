package main.java.nl.tudelft.contextproject.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import main.java.nl.tudelft.contextproject.ContextTFP;
import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.script.Script;
import main.java.nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the script creation screen.
 * 
 * @author Steven Meijer
 */
public class CreateScriptController {

    Script script;

    @FXML private Button btnAdd;
    @FXML private Button btnBack;

    @FXML private ChoiceBox<Integer> addCamera;
    @FXML private ChoiceBox<Integer> addPreset;

    @FXML private TableView<Shot> tableEvents;
    @FXML private TableColumn<Shot, String> tAdd;
    @FXML private TableColumn<Shot, Integer> tCamera;
    @FXML private TableColumn<Shot, String> tDescription;
    @FXML private TableColumn<Shot, Integer> tID;
    @FXML private TableColumn<Shot, Integer> tPreset;
    @FXML private TableColumn<Shot, String> tShot;

    @FXML private TextField addShot;
    @FXML private TextField addDescription;

    @FXML
    private void initialize() {

        //TEMP
        Camera c = new Camera();
        c.addPreset(new InstantPreset(new CameraSettings(), 0));
        c.addPreset(new InstantPreset(new CameraSettings(), 1));
        c.addPreset(new InstantPreset(new CameraSettings(), 2));
        //

        script = new Script(new ArrayList<Shot>());

        List<Integer> cameraList = new ArrayList<Integer>();
        List<Integer> presetList = new ArrayList<Integer>();

        for (int i = 0; i < Camera.getCameraAmount(); ++i) {
            cameraList.add(i + 1);
        }

        for (int i = 0; i < 3; ++i) {
            presetList.add(i + 1);
        }

        addCamera.setItems(FXCollections.observableArrayList(cameraList));
        addPreset.setItems(FXCollections.observableArrayList(presetList));

        //Disallow horizontal scrolling
        tableEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setFactories();
        setActions();
    }

    private void setFactories() {
        tID.setCellValueFactory(
                new PropertyValueFactory<Shot, Integer>("number"));

        tShot.setCellValueFactory(
                new PropertyValueFactory<Shot, String>("shotId"));

        tCamera.setCellValueFactory(new Callback<CellDataFeatures<Shot, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(CellDataFeatures<Shot, Integer> c) {
                return new ReadOnlyObjectWrapper<Integer>(c.getValue().getCamera().getNumber() + 1);
            }
        });

        tPreset.setCellValueFactory(new Callback<CellDataFeatures<Shot, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(CellDataFeatures<Shot, Integer> p) {
                return new ReadOnlyObjectWrapper<Integer>(p.getValue().getPreset().getId() + 1);
            }
        });

        tDescription.setCellValueFactory(
                new PropertyValueFactory<Shot, String>("description"));
    }

    private void setActions() {

        final ObservableList<Shot> data = FXCollections.observableArrayList();
        tableEvents.setItems(data);

        btnAdd.setOnAction((event) -> {
            boolean emptyField = false;

            if (addCamera.getSelectionModel().isEmpty()) {
                addCamera.setStyle("-fx-border-color: red;");
                emptyField = true;
            } else {
                addCamera.setStyle("");
            }

            if (addPreset.getSelectionModel().isEmpty()) {
                addPreset.setStyle("-fx-border-color: red;");
                emptyField = true;
            } else {
                addPreset.setStyle("");
            }

            if (addDescription.getText().isEmpty()) {
                addDescription.setStyle("-fx-border-color: red;");
                emptyField = true;
            } else {
                addPreset.setStyle("");
            }

            System.out.println(tableEvents.getItems().size() + 1);

            if (!emptyField) {
                Shot newShot = new Shot(
                        tableEvents.getItems().size() + 1,
                        addShot.getText(),
                        Camera.getCamera(addCamera.getSelectionModel().getSelectedItem() - 1),
                        Camera.getCamera(addCamera.getSelectionModel().getSelectedItem() - 1)
                            .getPreset(addPreset.getSelectionModel().getSelectedItem() - 1),
                        addDescription.getText()
                        );

                script.addShot(newShot);
                data.add(newShot);

                addShot.clear();
                addCamera.getSelectionModel().clearSelection();
                addPreset.getSelectionModel().clearSelection();
                addDescription.clear();
            }
        });

        btnBack.setOnAction((event) -> {
            MenuController.show();
        });
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/CreateScriptView.fxml"));
            AnchorPane createScriptView = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(createScriptView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
