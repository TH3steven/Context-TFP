package nl.tudelft.contextproject.gui;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class controls the screen that shows the live view
 * of the {@link Camera} for the director. The director can use this screen
 * to use the application during live production to get an overview
 * of the live camera, a preview camera and the {@link Script}, along with controls
 * to control the cameras and the {@link Preset Presets} set to these cameras.
 * 
 * <p>The view section is defined under view/DirectorLiveView.fxml
 * 
 * @since 0.3
 */
public class DirectorLiveController {

    private static Script script;

    @FXML private Button btnBack;
    @FXML private Button btnConfirm;
    @FXML private Button btnNext;
    @FXML private Button btnUndo;
    
    @FXML private CheckBox automaticCheck;
    
    @FXML private ChoiceBox<Camera> cameraSelecter;
    @FXML private ChoiceBox<String> presetSelecter;

    @FXML private ImageView thumbnail;
    
    @FXML private Label actionTxt;
    @FXML private Label labelID;
    
    @FXML private TableView<Shot> tableShots;
    @FXML private TableColumn<Shot, Number> columnCamera;
    @FXML private TableColumn<Shot, String> columnSubject;
    @FXML private TableColumn<Shot, Number> columnID;
    @FXML private TableColumn<Shot, String> columnPreset;
    @FXML private TableColumn<Shot, String> columnShot;
    
    @FXML private TextArea actionArea;
    
    @FXML private TextField fieldShot;
    @FXML private TextField fieldSubject;

    @FXML private VBox thumbnailBox;

    private boolean live;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        script = ContextTFP.getScript();
        Shot current = getCurrentShot();
        
        if (!script.isEmpty()) {
            if (script.getCurrent() == -1) {
                script.next();
            }
            live = true;
        } else {
            live = false;
        }

        initializeButtons();
        if (script.getShots().size() > 0) {
            initializeEditButtons();
            initializeCheckbox();
            initializeChoiceBoxes();
            updateShotInfo(current);
            initializeTableListener();
            thumbnail.setImage(loadImage(current.getPreset().getImage()));
        } else {
            emptyInitialization();
        }
        setFactories();
        
        thumbnail.fitWidthProperty().bind(thumbnailBox.widthProperty());
        thumbnail.fitHeightProperty().bind(thumbnailBox.heightProperty());
        
        tableShots.setItems(FXCollections.observableArrayList(script.getShots()));
    }
    
    /**
     * Binds the width and height properties of an ImageView to the properties of a VBox..
     * 
     * @param imgView The ImageView whose properties should be bound.
     * @param box The target whose properties will be used.
     */
    private void bindImageToBox(ImageView imgView, VBox box) {
        imgView.fitWidthProperty().bind(box.widthProperty());
        imgView.fitHeightProperty().bind(box.heightProperty());        
    }

    /**
     * Initializes the back and exit buttons's onAction events.
     */
    private void initializeButtons() {
        btnBack.toFront();
        btnBack.setOnAction(event -> {
            live = false;
            MenuController.show();
        });
        
        btnNext.setOnAction(event -> {
            initializeLive();
            live = true;
            btnNext.setText("Next shot");
        });
    }
    
    /**
     * Initializes the edit script buttons.
     */
    private void initializeEditButtons() {
        btnUndo.setOnAction(event -> {
            updateShotInfo(tableShots.getSelectionModel().getSelectedItem());
        });
        
        btnConfirm.setOnAction((event) -> {
            changeShot();
        });
    }
    
    /**
     * Initialize the button that handles going live.
     */
    private void initializeLive() {
        btnNext.setOnAction(event -> {
            if (!endReached()) {
                script.next(automaticCheck.isSelected());
                // TODO: Move the current shot highlight in the table.
            } else {
                actionTxt.setText("End of script reached");
            }
        });        
    }
    
    /**
     * Initialize the checkbox.
     */
    private void initializeCheckbox() {
        automaticCheck.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV && script.getCurrent() > -1) {
                script.adjustAllCameras();
            }
        });
    }
    
    /**
     * Initializes the choice boxes for preset and camera.
     */
    private void initializeChoiceBoxes() {
        Camera current = getCurrentShot().getCamera();
        
        initializeCameraChoice();
        initializePresetChoice();
        updatePresetChoice(current);
        
        presetSelecter.setValue(Integer.toString(getCurrentShot().getPreset().getId() + 1));
    }
    
    /**
     * Initializes the camera choice box.
     */
    private void initializeCameraChoice() {
        cameraSelecter.setItems(FXCollections.observableArrayList(Camera.getAllCameras()));
        Camera current = getCurrentShot().getCamera();
        cameraSelecter.setValue(current);
        
        cameraSelecter.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            updatePresetChoice(newV);
            presetSelecter.setValue("None");
        });
    }
    
    /**
     * Initializes the preset choice box.
     */
    private void initializePresetChoice() {
        Camera current = cameraSelecter.getValue();
        
        updatePresetChoice(current);
        
        presetSelecter.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                if (newV.equals("None")) {
                    thumbnail.setImage(loadImage("black.png"));
                } else {
                    thumbnail.setImage(loadImage(current.getPreset(Integer.valueOf(newV) - 1).getImage()));
                }
            }
        });
    }
    
    /**
     * Updates the preset choice box to the presets of a given camera.
     * 
     * @param cam The camera the presets shown should belong to.
     */
    private void updatePresetChoice(Camera cam) {       
        ArrayList<String> presetList = new ArrayList<String>(); 
        
        presetList.add("None");

        for (int i = 0; i < cam.getPresetAmount(); ++i) {
            presetList.add(Integer.toString(i + 1));
        }

        presetSelecter.setItems(FXCollections.observableArrayList(presetList));

        presetSelecter.setValue("None");
    }
    
    /**
     * Initialize a table selection listener that updates the shot info and the thumbnail.
     */
    private void initializeTableListener() {
        tableShots.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                updateShotInfo(newV);
            }
        });
    }
    
    /**
     * Set the table column factories.
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

        columnSubject.setCellValueFactory(new PropertyValueFactory<Shot, String>("description"));

        columnCamera.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(
                cellData.getValue().getCamera().getNumber() + 1));
    }
    
    /**
     * Initialize the UI in the case when the script is empty.
     */
    private void emptyInitialization() {
        updateShotInfo(new Shot(0, "", Camera.DUMMY, ""));
        actionTxt.setText("No shots");
    }
    
    /**
     * Updates the shot info and image.
     */
    public void updateShotInfo(Shot shot) {
        labelID.setText(Integer.toString(shot.getNumber()));
        fieldShot.setText(shot.getShotId());
        cameraSelecter.setValue(shot.getCamera());
        fieldSubject.setText(shot.getDescription());
        actionArea.setText(shot.getDescription());
        
        if (shot.getPreset() != null) {
            presetSelecter.setValue(Integer.toString(shot.getPreset().getId() + 1));
            thumbnail.setImage(loadImage(shot.getPreset().getImage()));
        } else {
            presetSelecter.setValue("None");
            thumbnail.setImage(loadImage("black.png"));
        }
    }
    
    /**
     * Loads an image. Loads an error image if path is null or invalid.
     * 
     * @param path Image path.
     * @return The newly loaded image.
     */
    public Image loadImage(String path) {
        try {
            return new Image(path);
        } catch (IllegalArgumentException e) {
            return new Image("error.jpg");
        }
    }
    
    /**
     * Resizes the ImageView.
     * @param width The new width of the ImageView.
     * @param height The new height of the ImageView.
     * @param imageView The imageView that needs resizing.
     * @param streamHandler The LiveStreamHandler responsible for the stream.
     */
    private void fitImageViewSize(float width, float height, ImageView imageView, LiveStreamHandler streamHandler) {
        if (imageView.getImage() instanceof WritableImage && streamHandler.isPlaying()) {
            FloatProperty videoSourceRatioProperty = streamHandler.getRatio();
            float fitHeight = videoSourceRatioProperty.get() * width;
            if (fitHeight > height) {
                imageView.setFitHeight(height);
                double fitWidth = height / videoSourceRatioProperty.get();
                imageView.setFitWidth(fitWidth);
                imageView.setX((width - fitWidth) / 2);
                imageView.setY(0);
            } else {
                imageView.setFitWidth(width);
                imageView.setFitHeight(fitHeight);
                imageView.setY((height - fitHeight) / 2);
                imageView.setX(0);
            } 
        }
    }
    
    /**
     * Gives a boolean back depending on whether the end of the script has been reached.
     * 
     * @return True if the end of the script has been reached, else false.
     */
    public boolean endReached() {
        return !script.hasNext();
    }
    
    /**
     * Method for adding a preset to the view and the model.
     * @param id The id of the preset to add.
     */
    private void changeShot() {
        Shot shot = script.getShots().get(tableShots.getSelectionModel().getSelectedIndex());
        
        String shotID = fieldShot.getText();
        Camera cam = cameraSelecter.getValue();        
        int presNum = Integer.valueOf(presetSelecter.getValue()) - 1;
        Preset preset = cam.getPreset(presNum);
        String description = fieldSubject.getText();
        
        shot.setShotId(shotID);
        shot.setCamera(cam);
        shot.setPreset(preset);
        shot.setDescription(description);       
    }
    
    /**
     * Gets the current shot in the script.
     * Gives back the first shot if it is not live yet.
     * 
     * @return The current or first shot.
     */
    public Shot getCurrentShot() {        
        return (script.getCurrent() == -1) ? script.getNextShot() : script.getCurrentShot();
    }
    
    /**
     * Calling this method shows this view in the middle of the rootLayout,
     * forcing the current view to disappear.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/DirectorView.fxml"));
            AnchorPane cameraLiveUI = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(cameraLiveUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
