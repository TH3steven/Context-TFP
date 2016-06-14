package nl.tudelft.contextproject.gui;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

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
public class DirectorLiveController implements Observer {

    private static Script script;

    @FXML private Button btnBack;
    @FXML private Button btnNext;
    
    @FXML private CheckBox automaticCheck;

    @FXML private ImageView thumbnail;
    
    @FXML private Label actionTxt;
    
    @FXML private TableView<Shot> tableShots;
    @FXML private TableColumn<Shot, Number> columnCamera;
    @FXML private TableColumn<Shot, String> columnSubject;
    @FXML private TableColumn<Shot, Number> columnID;
    @FXML private TableColumn<Shot, String> columnPreset;
    @FXML private TableColumn<Shot, String> columnShot;

    private boolean live;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        script = ContextTFP.getScript();
        
        if (!script.isEmpty()) {
            if (script.getCurrent() == -1) {
                script.next();
            }
            live = true;
        } else {
            live = false;
        }

        actionTxt.setText(script.getShots().get(0).getDescription());
        initializeButtons();
        initializeCheckbox();
        setFactories();
        
        thumbnail.setImage(loadImage(script.getShots().get(0).getPreset().getImage()));
        
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
     * Initializes the swap and back onAction events.
     */
    private void initializeButtons() {
        btnBack.toFront();
        btnBack.setOnAction((event) -> {
            live = false;
            MenuController.show();
        });
        
        btnNext.setOnAction((event) -> {
            initializeLive();
            live = true;
            btnNext.setText("Next shot");
        });
    }
    
    private void initializeLive() {
        btnNext.setOnAction((event) -> {
            if (!endReached()) {
                script.next(automaticCheck.isSelected());
                updateShotInfo();
            } else {
                thumbnail.setImage(loadImage("black.png"));
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
     * Updates the shot info and image.
     */
    public void updateShotInfo() {
        Shot current = script.getCurrentShot();
        thumbnail.setImage(loadImage(current.getPreset().getImage()));
        actionTxt.setText(current.getDescription());
        System.out.println("kaas");
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
    
    public boolean endReached() {
        return !script.hasNext();
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

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("dingen");
        Shot current = script.getCurrentShot();
        thumbnail.setImage(new Image(current.getPreset().getImage()));
        actionTxt.setText(current.getDescription());        
    }
}
