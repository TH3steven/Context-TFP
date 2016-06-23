package nl.tudelft.contextproject.gui;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Controller for the window that can display camera feeds. This
 * class is responsible for handling the two steams that can be
 * displayed in a pop-up window.
 * 
 * <p>The view section is defined under view/CameraFeedsView.fxml
 * 
 * @since 0.8
 */
public class CameraFeedsController {

    private static LiveStreamHandler leftStreamHandler;
    private static LiveStreamHandler rightStreamHandler;

    @FXML private ChoiceBox<Camera> camChoiceOne;
    @FXML private ChoiceBox<Camera> camChoiceTwo;

    @FXML private ImageView viewOne;
    @FXML private ImageView viewTwo;

    @FXML private VBox streamBoxOne;
    @FXML private VBox streamBoxTwo;

    private Collection<Camera> cameras;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        cameras = Camera.getAllCameras();

        leftStreamHandler = new LiveStreamHandler();
        rightStreamHandler = new LiveStreamHandler();

        initChoiceBoxes();
        addStreamListeners();

        viewOne.setImage(loadImage("black.png"));
        viewTwo.setImage(loadImage("black.png"));

        fitImageView(viewOne, streamBoxOne);
        fitImageView(viewTwo, streamBoxTwo);
    }

    /**
     * Initializes the choice boxes.
     * Allows the user to choose his camera feed for each view.
     */
    private void initChoiceBoxes() {
        ObservableList<Camera> choices = FXCollections.observableArrayList();
        choices.add(Camera.DUMMY);
        Iterator<Camera> it = cameras.iterator();

        while (it.hasNext()) {
            choices.add(it.next());
        }

        camChoiceOne.setItems(choices);
        camChoiceTwo.setItems(choices);

        camChoiceOne.setValue(Camera.DUMMY);
        camChoiceTwo.setValue(Camera.DUMMY);

        addChoiceListener(camChoiceOne);
        addChoiceListener(camChoiceTwo);
    }

    /**
     * Add a ChangeListener to the ChoiceBox.
     * @param cb the ChoiceBox to add the listener to.
     */
    private void addChoiceListener(ChoiceBox<Camera> cb) {
        cb.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {

            LiveStreamHandler streamHandler;

            if (cb.equals(camChoiceOne)) {
                streamHandler = leftStreamHandler;
            } else {
                streamHandler = rightStreamHandler;
            }

            VBox outerBox = (VBox) cb.getParent().getParent();
            VBox innerBox = (VBox) outerBox.getChildren().get(1);
            ImageView oldStream = (ImageView) innerBox.getChildren().get(0);
            
            if (newV.hasConnection()) {
                updateStream(newV.getConnection().getStreamLink(), oldStream, streamHandler);   
            } else {
                blackView(oldStream, streamHandler);
            }
            

        });
    }

    /**
     * Adds listeners to the width and height properties of the stream containers
     * for scaling.
     */
    private void addStreamListeners() {
        streamBoxOne.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (leftStreamHandler != null) {
                fitImageViewSize(newValue.floatValue(), 
                        (float) streamBoxOne.getHeight(), 
                        (ImageView) streamBoxOne.getChildren().get(0), leftStreamHandler);
            }
        });

        streamBoxTwo.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (rightStreamHandler != null) {
                fitImageViewSize(newValue.floatValue(), 
                        (float) streamBoxTwo.getHeight(), 
                        (ImageView) streamBoxTwo.getChildren().get(0), rightStreamHandler);
            }
        });

        streamBoxOne.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (leftStreamHandler != null) {
                fitImageViewSize((float) streamBoxOne.getWidth(), 
                        newValue.floatValue(), 
                        (ImageView) streamBoxOne.getChildren().get(0), leftStreamHandler);
            }
        });

        streamBoxTwo.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (rightStreamHandler != null) {
                fitImageViewSize((float) streamBoxTwo.getWidth(), 
                        newValue.floatValue(), 
                        (ImageView) streamBoxTwo.getChildren().get(0), rightStreamHandler);
            }
        });
    }

    /**
     * Updates the camera stream to the stream referenced by the specified link.
     * @param streamLink the link to the video stream to be played next.
     */
    private void updateStream(String streamLink, ImageView imageView, LiveStreamHandler streamHandler) {
        VBox vBox = (VBox) imageView.getParent();

        if (streamHandler != null) {
            streamHandler.stop();

            ImageView newView = streamHandler.createImageView(streamLink, 1920, 1080);

            Platform.runLater(() -> {
                fitImageViewSize((float) vBox.getWidth(), (float) vBox.getHeight(), newView, streamHandler);
            }); 

            vBox.getChildren().clear();
            vBox.getChildren().add(newView);
            vBox.setStyle("-fx-border-color: transparent");

            if (!ContextTFP.hasVLC()) {
                fitImageView(newView, vBox);
            }

            streamHandler.start();
        }
    }

    /**
     * Loads a black image instead of a stream.
     * 
     * @param imgView The ImageView to set to a black image.
     * @param streamHandler The stream handler that should be stopped.
     */
    private void blackView(ImageView imgView, LiveStreamHandler streamHandler) {
        if (streamHandler != null) {
            streamHandler.stop();
        }

        imgView.setImage(loadImage("black.png"));
    }

    /**
     * Resizes the ImageView.
     * 
     * @param width The new width of the ImageView.
     * @param height The new height of the ImageView.
     * @param imageView The imageView that needs resizing.
     * @param streamHandler The LiveStreamHandler responsible for the stream.
     */
    private void fitImageViewSize(float width, float height, ImageView imageView, LiveStreamHandler streamHandler) {
        if (imageView.getImage() instanceof WritableImage 
                && streamHandler.isPlaying()) {
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
     * Fit ImageView to its VBox container.
     * 
     * @param imgView The ImageView that should be fit to its VBox.
     * @param vBox The VBox the ImageView should be bound to.
     */
    private void fitImageView(ImageView imgView, VBox vBox) {
        imgView.fitWidthProperty().bind(vBox.widthProperty());
        imgView.fitHeightProperty().bind(vBox.heightProperty());
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
            return new Image("error-x.png");
        }
    }

    /**
     * Close the streams.
     */
    public static void closeStreams() {
        if (leftStreamHandler != null) {
            leftStreamHandler.stop();
        }

        if (rightStreamHandler != null) {
            rightStreamHandler.stop();
        }
    }

    /**
     * Calling this method shows this view in the middle of the rootLayout,
     * forcing the current view to disappear.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/CameraFeedsView.fxml"));
            AnchorPane cameraLiveUI = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(cameraLiveUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
