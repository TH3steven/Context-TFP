package main.java.nl.tudelft.contextproject.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import main.java.nl.tudelft.contextproject.ContextTFP;
import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for the script creation screen.
 * This class is responsible for the functions of the create script screen, to
 * allow for easy creation and saving of a new script.
 * 
 * @since 0.2
 */
public class CreateScriptController {

    @FXML private Button btnAdd;
    @FXML private Button btnBack;

    @FXML private ChoiceBox<Integer> addCamera;
    @FXML private ChoiceBox<Integer> addPreset;

    @FXML private HBox paneEdit;

    @FXML private TableView<Shot> tableEvents;
    @FXML private TableColumn<Shot, Shot> columnAction;
    @FXML private TableColumn<Shot, Integer> columnCamera;
    @FXML private TableColumn<Shot, String> columnDescription;
    @FXML private TableColumn<Shot, Integer> columnID;
    @FXML private TableColumn<Shot, Integer> columnPreset;
    @FXML private TableColumn<Shot, String> columnShot;

    @FXML private TextField addShot;
    @FXML private TextField addDescription;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {

        //TEMP
        Camera c = new Camera();
        new Camera();
        c.addPreset(new InstantPreset(new CameraSettings(), 0));
        c.addPreset(new InstantPreset(new CameraSettings(), 1));
        c.addPreset(new InstantPreset(new CameraSettings(), 2));
        //

        setFactories();
        setActions();

        initCamera();
        initPreset();

        allowEditing();

        // Disallow horizontal scrolling.
        tableEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Allows the user to press ENTER to add a shot.
        btnAdd.setDefaultButton(true);
    }

    /**
     * Allows for editable rows in the table.
     */
    private void allowEditing() {
        columnShot.setCellFactory(TextFieldTableCell.forTableColumn());
        columnShot.setOnEditCommit( (event) -> {
            ((Shot) event.getTableView().getItems().get(
                    event.getTablePosition().getRow())
                    ).setShotId(event.getNewValue());

            ContextTFP.getScript().getShots().get(
                    event.getTablePosition().getRow()
                    ).setShotId(event.getNewValue());
        });

        columnDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDescription.setOnEditCommit( (event) -> {
            ((Shot) event.getTableView().getItems().get(
                    event.getTablePosition().getRow())
                    ).setDescription(event.getNewValue());

            ContextTFP.getScript().getShots().get(
                    event.getTablePosition().getRow()
                    ).setDescription(event.getNewValue());
        });
    }

    /**
     * Fills the choicebox for selecting a camera.
     */
    private void initCamera() {
        List<Integer> cameraList = new ArrayList<Integer>();

        for (int i = 0; i < Camera.getCameraAmount(); ++i) {
            cameraList.add(i + 1);
        }

        addCamera.setItems(FXCollections.observableArrayList(cameraList));
    }

    /**
     * Fills the choicebox for selecting a preset, given the selection
     * of a certain camera.
     */
    private void initPreset() {
        List<Integer> presetList = new ArrayList<Integer>();

        addCamera.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> obs, Integer oldV, Integer newV) {
                presetList.clear();

                if (newV != null) {
                    addPreset.setDisable(false);

                    for (int i = 0; i < Camera.
                            getCamera(addCamera.getSelectionModel().getSelectedIndex()).getPresetAmount(); ++i) {
                        presetList.add(i + 1);
                    }

                    addPreset.setItems(FXCollections.observableArrayList(presetList));
                } else {
                    addPreset.setDisable(true);
                }
            }
        });

        addPreset.setDisable(true);
    }

    /**
     * Sets the factories of the table columns, aka where they should
     * get their value from.
     */
    private void setFactories() {
        columnID.setCellValueFactory(
                new PropertyValueFactory<Shot, Integer>("number"));

        columnShot.setCellValueFactory(
                new PropertyValueFactory<Shot, String>("shotId"));

        columnCamera.setCellValueFactory(new Callback<CellDataFeatures<Shot, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(CellDataFeatures<Shot, Integer> c) {
                return new ReadOnlyObjectWrapper<Integer>(c.getValue().getCamera().getNumber() + 1);
            }
        });

        columnPreset.setCellValueFactory(new Callback<CellDataFeatures<Shot, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(CellDataFeatures<Shot, Integer> p) {
                return new ReadOnlyObjectWrapper<Integer>(p.getValue().getPreset().getId() + 1);
            }
        });

        columnDescription.setCellValueFactory(
                new PropertyValueFactory<Shot, String>("description"));

        columnAction.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );

        columnAction.setCellFactory( param -> new TableCell<Shot, Shot>() {
            Button deleteButton = new Button("Remove");
            
            @Override
            protected void updateItem(Shot shot, boolean empty) {
                super.updateItem(shot, empty);

                if (shot == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(deleteButton);
                
//                deleteButton.setOnAction( (event) -> {
//                    getTableView().getItems().remove(shot);
//                    
//                    ContextTFP.getScript().getShots().remove(event.getTablePosition().getRow());
//                });
            }
        });
    }

    /**
     * Sets the actions to be taken when a button is pressed.
     */
    private void setActions() {
        final ObservableList<Shot> data = FXCollections.observableArrayList();
        
        tableEvents.setItems(data);
        
        btnAdd.setOnAction( event -> {
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

            if (!emptyField) {
                Shot newShot = new Shot(
                        tableEvents.getItems().size() + 1,
                        addShot.getText(),
                        Camera.getCamera(addCamera.getSelectionModel().getSelectedIndex()),
                        Camera.getCamera(addCamera.getSelectionModel().getSelectedIndex())
                        .getPreset(addPreset.getSelectionModel().getSelectedIndex()),
                        addDescription.getText()
                        );

                ContextTFP.getScript().addShot(newShot);
                data.add(newShot);

                addShot.clear();
                addCamera.getSelectionModel().clearSelection();
                addPreset.getSelectionModel().clearSelection();
                addDescription.clear();
            }
        });

        btnBack.setOnAction( event -> {
            if (!tableEvents.getItems().isEmpty()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirm quitting");
                alert.setHeaderText("Exiting will erase made changes");
                alert.setContentText("Are you sure you want to quit? Any unsaved changes "
                        + "will not be saved");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }

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
