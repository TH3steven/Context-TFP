package nl.tudelft.contextproject.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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
 * allow for easy creation and saving of a new script, and editing of an existing
 * script.
 * 
 * <p>The view section is defined under view/CreateScriptView.fxml
 * 
 * @since 0.2
 */
public class CreateScriptController {

    private static final String REDBORDER = "-fx-border-color: red;";
    private static boolean fill = false;

    private ObjectProperty<TableRow<Shot>> lastSelectedRow;
    private List<Shot> backupList;
    private int maximumId = 0;

    @FXML private Button btnAdd;
    @FXML private Button btnBack;
    @FXML private Button btnEditConfirm;
    @FXML private Button btnEditRemove;
    @FXML private Button btnSave;
    @FXML private Button btnSaveAs;

    @FXML private ChoiceBox<Number> addCamera;
    @FXML private ChoiceBox<String> addPreset;
    @FXML private ChoiceBox<Number> editCamera;
    @FXML private ChoiceBox<String> editPreset;

    @FXML private HBox editBox;

    @FXML private Pane editBlockScroll;

    @FXML private TableView<Shot> tableEvents;
    @FXML private TableColumn<Shot, Shot> columnAction;
    @FXML private TableColumn<Shot, Number> columnCamera;
    @FXML private TableColumn<Shot, String> columnSubject;
    @FXML private TableColumn<Shot, String> columnShotAction;
    @FXML private TableColumn<Shot, Number> columnID;
    @FXML private TableColumn<Shot, String> columnPreset;
    @FXML private TableColumn<Shot, Image> columnReorder;
    @FXML private TableColumn<Shot, String> columnShot;

    @FXML private TextField addShot;
    @FXML private TextField addSubject;
    @FXML private TextField addAction;
    @FXML private TextField editAction;
    @FXML private TextField editShot;
    @FXML private TextField editSubject;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {

        // Set the current script as backup.
        backupList = new ArrayList<Shot>();
        backupList.addAll(0, ContextTFP.getScript().getShots());

        setFactories();
        setAddButton();
        setBackButton();
        setSaveButton();

        initCamera();
        initPreset("add");
        initPreset("pres");

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
    private void initPreset(String prepend) {
        final List<String> presetList = new ArrayList<String>();
        final ChoiceBox<Number> cam = (prepend.equals("add")) ? addCamera : editCamera;
        final ChoiceBox<String> preset = (prepend.equals("add")) ? addPreset : editPreset;

        cam.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            presetList.clear();
            presetList.add("None");

            if (newV != null) {
                preset.setDisable(false);

                for (int i = 0; i < Camera.
                        getCamera(cam.getSelectionModel().getSelectedIndex()).getPresetAmount(); ++i) {
                    presetList.add(Integer.toString(i + 1));
                }

                preset.setItems(FXCollections.observableArrayList(presetList));
            } else {
                preset.setDisable(true);
            }
        });

        addPreset.setDisable(true);
    }

    /** Changed here.
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

        columnSubject.setCellValueFactory(
            new PropertyValueFactory<Shot, String>("description"));

        columnShotAction.setCellValueFactory(
            new PropertyValueFactory<Shot, String>("action"));

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
                });
            }
        });
    }

    /** Changed here -> addAction.clear and addAction.setStyle("").
     * Sets the onAction for the add new camera button.
     */
    private void setAddButton() {
        final ObservableList<Shot> data = FXCollections.observableArrayList();

        tableEvents.setItems(data);

        btnAdd.setOnAction(event -> {
            if (isValidInput()) {
                addCamera.setStyle("");
                addPreset.setStyle("");
                addSubject.setStyle("");
                addAction.setStyle("");

                if (!validateScript(data)
                        && !AlertDialog.confirmInvalidScriptAdding(
                                addCamera.getSelectionModel().getSelectedItem())) {
                    return;
                }

                Shot newShot = createNewShot();
                data.add(newShot);

                addShot.clear();
                addCamera.getSelectionModel().clearSelection();
                addPreset.getSelectionModel().clearSelection();
                addSubject.clear();
                addAction.clear();
            }
        });
    }

    /**
     * Checks if the new shot being added to the script
     * is valid.
     * 
     * @return True iff it has no errors.
     */
    private boolean isValidInput() {
        boolean isValid = true;

        if (addCamera.getSelectionModel().isEmpty()) {
            addCamera.setStyle(REDBORDER);
            isValid = false;
        }

        if (addPreset.getSelectionModel().isEmpty()) {
            addPreset.setStyle(REDBORDER);
            isValid = false;
        }

        if (addSubject.getText().isEmpty()) {
            addSubject.setStyle(REDBORDER);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Checks if a newly created shot does not create an
     * invalid script.
     * 
     * @param data The table data.
     * @return True if the script is valid, false otherwise.
     */
    private boolean validateScript(List<Shot> data) {
        if (data.isEmpty()) {
            return true;
        }
        
        Shot last = data.get(data.size() - 1);
        
        if (last.getCamera().getNumber() 
                == addCamera.getSelectionModel().getSelectedIndex()) {
            return false;
        }

        return true;
    }

    /** Changed here -> addaction.getText
     * Creates a new shot based on the users' input.
     * @return The newly created shot.
     */
    private Shot createNewShot() {
        maximumId++;
        
        if (addPreset.getSelectionModel().getSelectedItem().equals("None")) {
            final Shot newShot = new Shot(
                    maximumId,
                    addShot.getText(),
                    Camera.getCamera(addCamera.getSelectionModel().getSelectedIndex()),
                    addSubject.getText(),
                    addAction.getText()
                    );

            return newShot;
        } else {
            final Shot newShot = new Shot(
                    maximumId,
                    addShot.getText(),
                    Camera.getCamera(addCamera.getSelectionModel().getSelectedIndex()),
                    Camera.getCamera(addCamera.getSelectionModel().getSelectedIndex())
                        .getPreset(new Integer(addPreset.getSelectionModel().getSelectedItem()) - 1),
                    addSubject.getText(),
                    addAction.getText()
                    );

            return newShot;
        }
    }

    /**
     * Sets the onAction for the back button.
     */
    private void setBackButton() {
        btnBack.setOnAction(event -> {
            if (!tableEvents.getItems().isEmpty() && !AlertDialog.confirmExiting()) {
                return;
            }

            Script backup = new Script(backupList);
            backup.setName(ContextTFP.getScript().getName());

            ContextTFP.setScript(backup);
            MenuController.show();
        });
    }

    /**
     * Sets the onAction for the save buttons.
     */
    private void setSaveButton() {
        if (backupList.isEmpty()) {
            btnSave.setDisable(true);
        }

        btnSave.setOnAction(event -> {
            setSaveAction(event, false);
        });

        btnSaveAs.setOnAction(event -> {
            setSaveAction(event, true);
        });
    }

    /**
     * Handles the saving of a file.
     */
    private void setSaveAction(ActionEvent event, boolean showDialog) {
        final Script script = new Script(tableEvents.getItems());

        if (!showValid(script, 1)) {
            return;
        }

        File file;

        if (showDialog) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save script");
            fileChooser.setInitialFileName("script");
            fileChooser.getExtensionFilters().add(new ExtensionFilter("XML (*.xml)", "*.xml"));

            file = fileChooser.showSaveDialog(((Node) event.getTarget()).getScene().getWindow());
        } else {
            file = new File(SaveScript.getSaveLocation());
        }

        if (file != null) {
            try {
                SaveScript.setSaveLocation(file.getAbsolutePath());
                SaveScript.save(script);

                script.setName(file.getName());
                ContextTFP.setScript(script);

                AlertDialog.confirmExitingAfterSaving(file);
            } catch (Exception e) {
                AlertDialog.errorSaveUnsuccesful(e, file);
            }
        }
    }
    
    /**
     * Checks if a script is valid and gives an error message when it isn't.
     * 
     * @param level The level of alert. Should be 1 for CONFIRMATION
     *      or 2 for WARNING. Other values are ignored.
     * @return True if the user wants to continue and ignore the error.
     */
    public static boolean showValid(Script script, int level) {
        Shot error = script.isValid();

        if (error != null) {
            Alert alert = null;

            if (level == 1) {
                alert = AlertDialog.confirmInvalidScriptSaving(error);
            } else if (level == 2) {
                alert = AlertDialog.warningInvalidScriptLoading(error);
            } else {
                return true;
            }

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.CANCEL) {
                return false;
            }
        }

        return true;
    }

    /** Changed here
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

        initEditButtons();

        EventHandler<KeyEvent> addResourceHandler = event -> {
            if (lastSelectedRow.get() != null && event.getCode() == KeyCode.ENTER) {
                editConfirmAction(lastSelectedRow.get().getItem());
            }
        };

        editBox.setOnKeyPressed(addResourceHandler);
        editShot.setOnKeyReleased(addResourceHandler);
        editCamera.setOnKeyReleased(addResourceHandler);
        editPreset.setOnKeyReleased(addResourceHandler);
        editSubject.setOnKeyReleased(addResourceHandler);
        editAction.setOnKeyReleased(addResourceHandler);

        initTable();
    }

    /** Changed here.
     * Sets the onAction for the edit confirm and
     * edit remove buttons that show up when editing
     * a shot.
     */
    private void initEditButtons() {
        btnEditConfirm.setOnAction(event -> {
            boolean isValid = true;

            if (editCamera.getSelectionModel().isEmpty()) {
                editCamera.setStyle(REDBORDER);
                isValid = false;
            }

            if (editPreset.getSelectionModel().isEmpty()) {
                editPreset.setStyle(REDBORDER);
                isValid = false;
            }

            if (editSubject.getText().isEmpty()) {
                editSubject.setStyle(REDBORDER);
                isValid = false;
            }

            if (editAction.getText().isEmpty()) {
                editAction.setStyle(REDBORDER);
                isValid = false;
            }

            if (isValid) {
                editCamera.setStyle("");
                editPreset.setStyle("");
                editSubject.setStyle("");

                editConfirmAction(lastSelectedRow.get().getItem());
            }
        });

        btnEditRemove.setOnAction(event -> {
            Shot shot = lastSelectedRow.get().getItem();
            tableEvents.getItems().remove(shot);

            editDoneAction();
        });
    }

    /**Changed here
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
                editSubject.setText(nv.getDescription());
                editAction.setText(nv.getAction());

                if (ov != nv && ov != null) {
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

    /** Changed here.
     * Sets the action to be taken when an edit is complete.
     * @param shot The shot that is edited.
     */
    private void editConfirmAction(Shot shot) {
        Shot backup = new Shot(shot.getNumber(), shot.getShotId(), 
                shot.getCamera(), shot.getPreset(), shot.getDescription(), shot.getAction());

        shot.setShotId(editShot.getText());
        shot.setCamera(Camera.getCamera(editCamera.getSelectionModel().getSelectedIndex()));
        if (!editPreset.getSelectionModel().getSelectedItem().equals("None")) {
            shot.setPreset(Camera.getCamera(editCamera.getSelectionModel().getSelectedIndex())
                    .getPreset(new Integer(editPreset.getSelectionModel().getSelectedItem()) - 1));
        } else {
            shot.setPreset(null);
        }
        shot.setDescription(editSubject.getText());
        shot.setAction(editAction.getText());

        if (lastSelectedRow.get().getIndex() < backupList.size()) {
            backupList.set(lastSelectedRow.get().getIndex(), backup);
        }

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
     * @param table The table itself.
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
     * @param cell The cell in the tableView.
     * @param table The table itself.
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
