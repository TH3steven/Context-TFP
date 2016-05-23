package nl.tudelft.contextproject.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.saveLoad.SaveScript;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Controller class for the script creation screen.
 * This class is responsible for the functions of the create script screen, to
 * allow for easy creation and saving of a new script.
 * 
 * <p>The view section is defined under view/CreateScriptView.fxml
 * 
 * @since 0.2
 */
public class CreateScriptController {

    private static boolean fill = false;

    private ObjectProperty<TableRow<Shot>> lastSelectedRow;
    private Script script;
    private int maximumId = 1;

    @FXML private Button btnAdd;
    @FXML private Button btnBack;
    @FXML private Button btnEditConfirm;
    @FXML private Button btnEditRemove;
    @FXML private Button btnSave;

    @FXML private ChoiceBox<Number> addCamera;
    @FXML private ChoiceBox<String> addPreset;
    @FXML private ChoiceBox<Number> editCamera;
    @FXML private ChoiceBox<String> editPreset;

    @FXML private HBox editBox;

    @FXML private Pane editBlockScroll;

    @FXML private TableView<Shot> tableEvents;
    @FXML private TableColumn<Shot, Shot> columnAction;
    @FXML private TableColumn<Shot, Number> columnCamera;
    @FXML private TableColumn<Shot, String> columnDescription;
    @FXML private TableColumn<Shot, Number> columnID;
    @FXML private TableColumn<Shot, String> columnPreset;
    @FXML private TableColumn<Shot, Image> columnReorder;
    @FXML private TableColumn<Shot, String> columnShot;

    @FXML private TextField addShot;
    @FXML private TextField addDescription;
    @FXML private TextField editShot;
    @FXML private TextField editDescription;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        setFactories();
        setAddButton();
        setBackButton();
        setSaveButton();

        initCamera();
        initPreset();

        allowEditing();
        allowRowReordering();

        // Disallow horizontal scrolling.
        tableEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Allows the user to press ENTER to add a shot.
        btnAdd.setDefaultButton(true);

        // Removes the "No content in table" label.
        tableEvents.setPlaceholder(new Label(""));

        if (fill) {
            fillTable(ContextTFP.getScript().getShots());
            fill = false;
        }
        
        // Makes sure there are no duplicate ID's of the shots in the table.
        for (Shot s : tableEvents.getItems()) {
            if (s.getNumber() > maximumId) {
                maximumId = s.getNumber();
            }
        }

        // Store the current script locally to reduce traffic.
        script = ContextTFP.getScript();
    }

    /**
     * Sets if the table should be filled beforehand. This method is used to 
     * enable editing of the currently active script.
     * 
     * <p>When the argument is {@code true}, the {@link #fillTable(List)} method
     * is called after this view is loaded.
     * 
     * @param bFill If the table should be filled.
     */
    public static void setFill(boolean bFill) {
        fill = bFill;
    }

    /**
     * Fills the tableView of the create script screen with the shots
     * listed in the argument. It makes sure these elements get displayed
     * in the table.
     * 
     * @param shots A {@link List} of shots that the tableView should be
     *      filled with.
     */
    private void fillTable(List<Shot> shots) {
        tableEvents.getItems().addAll(shots);
    }

    /**
     * Allows for editable rows in the table. This method defines the
     * necessary components for row by row editing of the table.
     */
    private void allowEditing() {
        editBox.setVisible(false);
        editBox.toBack();

        // Allows for getting the last selected row.
        lastSelectedRow = new SimpleObjectProperty<>();
        tableEvents.setRowFactory(tableView -> {
            TableRow<Shot> row = new TableRow<Shot>();

            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    lastSelectedRow.set(row);
                }
            });

            return row;
        });

        editBlockScroll.setOnMouseClicked(event -> {
            editBlockScroll.setVisible(false);
            editConfirmAction(lastSelectedRow.get().getItem());
        });

        btnEditConfirm.setOnAction(event -> {
            boolean emptyField = false;

            if (editCamera.getSelectionModel().isEmpty()) {
                editCamera.setStyle("-fx-border-color: red;");
                emptyField = true;
            }

            if (editPreset.getSelectionModel().isEmpty()) {
                editPreset.setStyle("-fx-border-color: red;");
                emptyField = true;
            }

            if (editDescription.getText().isEmpty()) {
                editDescription.setStyle("-fx-border-color: red;");
                emptyField = true;
            }

            if (!emptyField) {
                editCamera.setStyle("");
                editPreset.setStyle("");
                editDescription.setStyle("");

                editConfirmAction(lastSelectedRow.get().getItem());
            }
        });

        btnEditRemove.setOnAction(event -> {
            Shot shot = lastSelectedRow.get().getItem();
            tableEvents.getItems().remove(shot);

            editDoneAction();
        });

        EventHandler<KeyEvent> addResourceHandler = event -> {
            if (lastSelectedRow.get() != null && event.getCode() == KeyCode.ENTER) {
                editConfirmAction(lastSelectedRow.get().getItem());
            }
        };

        editBox.setOnKeyPressed(addResourceHandler);
        editShot.setOnKeyReleased(addResourceHandler);
        editCamera.setOnKeyReleased(addResourceHandler);
        editPreset.setOnKeyReleased(addResourceHandler);
        editDescription.setOnKeyReleased(addResourceHandler);

        initTable();
    }

    /**
     * Sets listeners and actions on the table.
     */
    private void initTable() {
        tableEvents.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {                
                editShot.setText(nv.getShotId());
                editCamera.getSelectionModel().select(nv.getCamera().getNumber());
                if (nv.getPreset() != null) {
                    editPreset.getSelectionModel().select(nv.getPreset().getId() + 1);
                } else {
                    editPreset.getSelectionModel().select(0);
                }
                editDescription.setText(nv.getDescription());

                if (ov != nv && ov != null) {
                    // Commit edit?
                    editDoneAction();
                }
            }
        });

        tableEvents.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && lastSelectedRow.get() != null) {
                // Make sure the HBox is drawn over the selected row.
                editBox.setLayoutY(lastSelectedRow.get().getLayoutY()
                        + editBox.getTranslateY()
                        + 40);

                editBlockScroll.setVisible(true);
                editBox.toFront();
                editBox.setVisible(true);
            }
        });
    }

    /**
     * Sets the action to be taken when an edit is complete.
     * @param shot The shot that is edited.
     */
    private void editConfirmAction(Shot shot) {
        shot.setShotId(editShot.getText());
        shot.setCamera(Camera.getCamera(editCamera.getSelectionModel().getSelectedIndex()));
        if (!editPreset.getSelectionModel().getSelectedItem().equals("None")) {
            shot.setPreset(Camera.getCamera(editCamera.getSelectionModel().getSelectedIndex())
                    .getPreset(new Integer(editPreset.getSelectionModel().getSelectedItem()) - 1));
        } else {
            shot.setPreset(null);
        }
        shot.setDescription(editDescription.getText());

        editDoneAction();
    }

    /**
     * Sets the actions to be done when editing a row is finished.
     */
    private void editDoneAction() {
        editBox.setVisible(false);
        editBlockScroll.setVisible(false);
        editBox.toBack();
        tableEvents.refresh();
    }

    /**
     * Allows the reordering of rows. This uses a special column in the tableView,
     * that is filled with an image.
     */
    private void allowRowReordering() {
        final Callback<TableColumn<Shot, Image>, TableCell<Shot, Image>> cellFactory = callback -> {
            return new TableCell<Shot, Image>() {
                {
                    setOnDragDetected(createDragDetectedHandler(this));
                    setOnDragOver(createDragOverHandler(this, tableEvents));
                    setOnDragDropped(createDragDroppedHandler(this, tableEvents));
                }

                @Override
                public void updateItem(Image item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setText(null);
                    } else {
                        ImageView imageview = new ImageView();
                        imageview.setImage(new Image("reorder.png"));
                        imageview.setFitHeight(20);
                        imageview.setFitWidth(10);
                        setGraphic(imageview);
                        setItem(item);
                    }
                }
            };
        };

        columnReorder.setCellFactory(cellFactory);
    }

    /**
     * The next three methods define the functionality of the drag and drop actions that can
     * be called by initializing a drag and drop action on the image that is intended for this
     * functionality.
     * 
     * <p>Most credits go to James_D for creating the original code.
     * 
     * @param cell The cell in the tableView.
     * @return Returns an event that holds the start of a Drag and Drop action.
     * @see <a href="https://community.oracle.com/message/11057757#11057757">Oracle Community</a>
     */
    private EventHandler<MouseEvent> createDragDetectedHandler(TableCell<Shot, ?> cell) {
        return event -> {
            final Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
            final ClipboardContent content = new ClipboardContent();
            
            content.putString(String.valueOf(cell.getIndex()));
            db.setContent(content);
        };
    }

    /**
     * @param cell The cell in the tableView.
     * @param table The table itself
     * @return Returns an event that contains the drag action.
     * @see #createDragDetectedHandler(TableCell)
     */
    private EventHandler<DragEvent> createDragOverHandler(TableCell<Shot, ?> cell, final TableView<Shot> table) {
        return event -> {
            final String INTEGER_REGEX = "-?\\d+";
            final Dragboard dragboard = event.getDragboard();

            if (dragboard.hasString()) {
                final String value = dragboard.getString();
                
                if (Pattern.matches(INTEGER_REGEX, value)) {
                    try {
                        final int index = Integer.parseInt(value);
                        
                        if (index != cell.getIndex()
                                && index != -1
                                && (index < table.getItems().size() - 1 || cell.getIndex() != -1)) {
                            event.acceptTransferModes(TransferMode.MOVE);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * 
     * @param cell The cell in the tableView.
     * @param table The table itself
     * @return Returns an event that handles the dropped action.
     * @see #createDragDetectedHandler(TableCell)
     */
    private EventHandler<DragEvent> createDragDroppedHandler(TableCell<Shot, ?> cell, TableView<Shot> table) {
        return event -> {
            final Dragboard db = event.getDragboard();
            int myIndex = cell.getIndex();

            if (myIndex < 0 || myIndex >= table.getItems().size()) {
                myIndex = table.getItems().size() - 1;
            }

            final int incomingIndex = Integer.parseInt(db.getString());
            table.getItems().add(myIndex, table.getItems().remove(incomingIndex));
            event.setDropCompleted(true);
        };
    }

    /**
     * Fills the choiceboxes for selecting a camera, both the box that
     * adds a new camera as the box that is shown when editing a shot.
     */
    private void initCamera() {
        final List<Number> cameraList = new ArrayList<Number>();

        for (int i = 0; i < Camera.getCameraAmount(); ++i) {
            cameraList.add(i + 1);
        }

        addCamera.setItems(FXCollections.observableArrayList(cameraList));
        editCamera.setItems(FXCollections.observableArrayList(cameraList));
    }

    /**
     * Fills the choiceboxes for selecting a preset, given the selection
     * of a certain camera.
     */
    private void initPreset() {
        final List<String> presetList = new ArrayList<String>();

        addCamera.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            presetList.clear();
            presetList.add("None");

            if (newV != null) {
                addPreset.setDisable(false);

                for (int i = 0; i < Camera.
                        getCamera(addCamera.getSelectionModel().getSelectedIndex()).getPresetAmount(); ++i) {
                    presetList.add(Integer.toString(i + 1));
                }

                addPreset.setItems(FXCollections.observableArrayList(presetList));
            } else {
                addPreset.setDisable(true);
            }
        });

        editCamera.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            presetList.clear();
            presetList.add("None");

            if (newV != null) {
                for (int i = 0; i < Camera.
                        getCamera(editCamera.getSelectionModel().getSelectedIndex()).getPresetAmount(); ++i) {
                    presetList.add(Integer.toString(i + 1));
                }

                editPreset.setItems(FXCollections.observableArrayList(presetList));
            } else {
                editPreset.setDisable(true);
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
            new PropertyValueFactory<Shot, Number>("number"));

        columnShot.setCellValueFactory(
            new PropertyValueFactory<Shot, String>("shotId"));

        columnCamera.setCellValueFactory(cellData ->
            new ReadOnlyObjectWrapper<>(cellData.getValue().getCamera().getNumber() + 1));

        columnPreset.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPreset() == null) {
                return new ReadOnlyObjectWrapper<>();
            } else {
                return new ReadOnlyObjectWrapper<>(
                        Integer.toString(cellData.getValue().getPreset().getId() + 1));
            }
        });

        columnDescription.setCellValueFactory(
            new PropertyValueFactory<Shot, String>("description"));

        columnAction.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue()));

        columnAction.setCellFactory(cellData -> new TableCell<Shot, Shot>() {
            final Button btnRemove = new Button("Remove");

            @Override
            protected void updateItem(Shot shot, boolean empty) {
                super.updateItem(shot, empty);

                if (shot == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(btnRemove);

                btnRemove.setOnAction(event -> {
                    getTableView().getItems().remove(shot);
                    script.getShots().remove(shot);
                });
            }
        });
    }

    /**
     * Sets the onAction for the add new camera button.
     */
    private void setAddButton() {
        final ObservableList<Shot> data = FXCollections.observableArrayList();

        tableEvents.setItems(data);

        btnAdd.setOnAction(event -> {
            boolean emptyField = false;

            if (addCamera.getSelectionModel().isEmpty()) {
                addCamera.setStyle("-fx-border-color: red;");
                emptyField = true;
            }

            if (addPreset.getSelectionModel().isEmpty()) {
                addPreset.setStyle("-fx-border-color: red;");
                emptyField = true;
            }

            if (addDescription.getText().isEmpty()) {
                addDescription.setStyle("-fx-border-color: red;");
                emptyField = true;
            }

            if (!emptyField) {
                addCamera.setStyle("");
                addPreset.setStyle("");
                addDescription.setStyle("");

                if (addPreset.getSelectionModel().getSelectedItem().equals("None")) {
                    final Shot newShot = new Shot(
                            maximumId,
                            addShot.getText(),
                            Camera.getCamera(addCamera.getSelectionModel().getSelectedIndex()),
                            addDescription.getText()
                            );

                    data.add(newShot);
                    script.addShot(newShot);
                } else {
                    final Shot newShot = new Shot(
                            maximumId,
                            addShot.getText(),
                            Camera.getCamera(addCamera.getSelectionModel().getSelectedIndex()),
                            Camera.getCamera(addCamera.getSelectionModel().getSelectedIndex())
                                .getPreset(new Integer(addPreset.getSelectionModel().getSelectedItem()) - 1),
                            addDescription.getText()
                            );

                    data.add(newShot);
                    script.addShot(newShot);
                }
                
                maximumId++;

                addShot.clear();
                addCamera.getSelectionModel().clearSelection();
                addPreset.getSelectionModel().clearSelection();
                addDescription.clear();
            }
        });
    }

    /**
     * Sets the onAction for the back button.
     */
    private void setBackButton() {
        btnBack.setOnAction(event -> {
            if (!tableEvents.getItems().isEmpty()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirm quitting");
                alert.setHeaderText("Exiting will erase any unsaved changes");
                alert.setContentText("Are you sure you want to quit? Any unsaved changes "
                        + "will not be kept.");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }

            MenuController.show();
        });
    }

    /**
     * Sets the onAction for the save button.
     */
    private void setSaveButton() {
        btnSave.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save script");
            fileChooser.setInitialFileName("script");
            fileChooser.getExtensionFilters().add(new ExtensionFilter("XML (*.xml)", "*.xml"));

            final File file = fileChooser.showSaveDialog(((Node) event.getTarget()).getScene().getWindow());

            if (file != null) {
                try {
                    SaveScript.setSaveLocation(file.getAbsolutePath());
                    SaveScript.save(script);

                    script.setName(file.getName());
                    ContextTFP.setScript(script);

                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Confirm exiting");
                    alert.setHeaderText("Saving script was succesful!");
                    alert.setContentText("Succesful save of script: " 
                            + file.getName()
                            + " Do you want to quit to menu?");

                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.get() == ButtonType.OK) {
                        MenuController.show();
                    }

                } catch (Exception e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle(e.getMessage());
                    alert.setHeaderText("Saving script was unsuccesful!");
                    alert.setContentText("Error when trying to save script at location: " 
                            + file.getAbsolutePath()
                            + "\n\nError: "
                            + e.getCause());

                    alert.showAndWait();
                }
            }
        });
    }

    /**
     * Calling this method shows this view in the middle of the rootLayout,
     * forcing the current view to disappear.
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
