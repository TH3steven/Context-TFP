package nl.tudelft.contextproject.gui;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    @FXML private Button btnNext;
    @FXML private Button btnSwap;

    @FXML private Label bigCameraNumberLabel;
    @FXML private Label bigShotNumberLabel;
    @FXML private Label bigPresetLabel;
    @FXML private Label bigStatusLabel;
    @FXML private Label smallCameraNumberLabel;
    @FXML private Label smallPresetLabel;
    @FXML private Label smallShotNumberLabel;
    @FXML private Label smallStatusLabel;

    @FXML private TextArea bigDescriptionField;
    @FXML private TextArea smallDescriptionField;

    @FXML private VBox bigViewBox;
    @FXML private VBox smallViewBox;
    
    private boolean endReached;
    private boolean live;
    
    /**
     * True if the big screen shows the live view, otherwise false.
     */
    private boolean bigShowsLive;
    
    private LiveStreamHandler liveStreamHandler;
    private LiveStreamHandler nextStreamHandler;
    

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        script = ContextTFP.getScript();
        liveStreamHandler = new LiveStreamHandler();
        nextStreamHandler = new LiveStreamHandler();

        endReached = false;
        bigShowsLive = true;
        
        if (!script.isEmpty()) {
            if (script.getCurrent() == -1) {
                script.next();
            }
            live = true;
        } else {
            live = false;
        }

        addListenersToBoxes();
        initializeLabels();
        initializeButtons();
        
        if (live) {
            initializeLiveViews();
        } else {
            initializeBlackViews(false);
        }
    }
    
    /**
     * Adds the listeners to the viewboxes in order to get scalability.
     */
    private void addListenersToBoxes() {
        LiveStreamHandler smallStream;
        LiveStreamHandler bigStream;
        
        if (bigShowsLive) {
            smallStream = nextStreamHandler;
            bigStream = liveStreamHandler;
        } else {
            smallStream = liveStreamHandler;
            bigStream = nextStreamHandler;
        }
        
        smallViewBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize(newValue.floatValue(), (float) smallViewBox.getHeight(), 
                    (ImageView) smallViewBox.getChildren().get(0), smallStream);
        });
        
        smallViewBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) smallViewBox.getWidth(), newValue.floatValue(), 
                    (ImageView) smallViewBox.getChildren().get(0), smallStream);
        });
        
        bigViewBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize(newValue.floatValue(), (float) bigViewBox.getHeight(), 
                    (ImageView) bigViewBox.getChildren().get(0), bigStream);
        });
        
        bigViewBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) bigViewBox.getWidth(), newValue.floatValue(), 
                    (ImageView) bigViewBox.getChildren().get(0), bigStream);
        });
    }

    /**
     * Initializes the labels.
     */
    private void initializeLabels() {
        bigStatusLabel.setText("LIVE");
        bigStatusLabel.setStyle("-fx-text-fill: red;");
        smallStatusLabel.setText("Up next");

        updateTables();
    }

    /**
     * Initializes the live views.
     */
    private void initializeLiveViews() {
        createStream(script.getCurrentShot().getCamera().getConnection().getStreamLink(), 
                liveStreamHandler, bigViewBox);

        if (script.getNextShot() != null) {
            createStream(script.getNextShot().getCamera().getConnection().getStreamLink(), 
                    nextStreamHandler, smallViewBox);
        } else {
            initializeBlackViews(true);
        }
    }
    
    /**
     * Initializes the views with black screens,
     * @param smallOnly True if only the small view needs a black screen.
     */
    private void initializeBlackViews(boolean smallOnly) {
        ImageView img1 = new ImageView("black.png"); 
        bindImageToBox(img1, smallViewBox);
        smallViewBox.getChildren().clear();
        smallViewBox.getChildren().add(img1);
        
        if (!smallOnly) {
            ImageView img2 = new ImageView("black.png"); 
            bindImageToBox(img2, bigViewBox);
            bigViewBox.getChildren().clear();
            bigViewBox.getChildren().add(img2);
        }
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
        btnSwap.setOnAction((event) -> {
            swapAction();
        });

        btnBack.toFront();
        btnBack.setOnAction((event) -> {
            liveStreamHandler.stop();
            nextStreamHandler.stop();
            live = false;
            MenuController.show();
        });
        
        btnNext.setOnAction((event) -> {
            if (!endReached) {
                script.next();
                updateTables();
                nextViews();
            }
        });
    }
    
    /**
     * Performs the action to swap the two views.
     */
    private void swapAction() {
        bigShowsLive = !bigShowsLive;
        
        swapViews();

        String text = bigStatusLabel.getText();
        bigStatusLabel.setText(smallStatusLabel.getText());
        smallStatusLabel.setText(text);

        if (text.equals("LIVE")) {
            smallStatusLabel.setStyle("-fx-text-fill: red;");
            bigStatusLabel.setStyle("");
        } else {
            smallStatusLabel.setStyle("");
            bigStatusLabel.setStyle("-fx-text-fill: red;");
        }
        
        updateTables();
    }
    
    /**
     * Swaps the small and big view.
     */
    private void swapViews() {
        ImageView newBig = (ImageView) smallViewBox.getChildren().get(0);
        ImageView newSmall = (ImageView) bigViewBox.getChildren().get(0);
        smallViewBox.getChildren().clear();
        smallViewBox.getChildren().add(newSmall);
        bigViewBox.getChildren().clear();
        bigViewBox.getChildren().add(newBig); 
        bigViewBox.getChildren().get(0);
        swapScalability(newSmall, newBig);
    }
    
    /**
     * Implements the scalability after the views have been swapped.
     * 
     * @param small The ImageView that is displayed in the smallViewBox.
     * @param big The ImageView that is displayed in the bigViewBox.
     */
    private void swapScalability(ImageView small, ImageView big) {
        LiveStreamHandler smallStream;
        LiveStreamHandler bigStream;
        
        if (bigShowsLive) {
            smallStream = nextStreamHandler;
            bigStream = liveStreamHandler;
        } else {
            smallStream = liveStreamHandler;
            bigStream = nextStreamHandler;
        }
        
        if (big.getImage() instanceof WritableImage) {
            fitImageViewSize((float) bigViewBox.getWidth(), (float) bigViewBox.getHeight(), big, bigStream);
        } else {
            bindImageToBox(big, bigViewBox);
        }
        
        if (small.getImage() instanceof WritableImage) {
            fitImageViewSize((float) smallViewBox.getWidth(), (float) smallViewBox.getHeight(), small, smallStream);
        } else {
            bindImageToBox(small, smallViewBox);
        }
    }

    /**
     * Updates the table contents according to the current position in the script.
     */
    private void updateTables() {
        Shot smallShot;
        Shot bigShot;
        if (bigShowsLive) {
            smallShot = script.getNextShot();
            bigShot = script.getCurrentShot();
        } else {
            smallShot = script.getCurrentShot();
            bigShot = script.getNextShot();
        }
        
        if (smallShot != null) {
            smallShotNumberLabel.setText(smallShot.getShotId());
            smallCameraNumberLabel.setText(Integer.toString(smallShot.getCamera().getNumber()));
            smallPresetLabel.setText(Integer.toString(smallShot.getPreset().getId()));
            smallDescriptionField.setText(smallShot.getDescription());
        } else {
            initializeBlackViews(true);
            smallShotNumberLabel.setText("");
            smallCameraNumberLabel.setText("");
            smallPresetLabel.setText("");
            smallDescriptionField.setText("End of script reached");
            endReached = true;
        }

        if (bigShot != null) {
            bigShotNumberLabel.setText(bigShot.getShotId());
            bigCameraNumberLabel.setText(Integer.toString(bigShot.getCamera().getNumber()));
            bigPresetLabel.setText(Integer.toString(bigShot.getPreset().getId()));
            bigDescriptionField.setText(bigShot.getDescription());
        } 
    }
    
    /**
     * Updates the live views of the cameras when you go to the next shot.
     */
    private void nextViews() {
        Shot nextShot = script.getNextShot();
        
        liveStreamHandler = nextStreamHandler;
        
        if (bigShowsLive) {
            bigViewBox.getChildren().clear();
            createStream(liveStreamHandler.getStreamLink(), liveStreamHandler, bigViewBox);
            smallViewBox.getChildren().clear();
            if (nextShot != null) {
                createStream(nextShot.getCamera().getConnection().getStreamLink(), nextStreamHandler, smallViewBox);
            } else {
                initializeBlackViews(true);
                nextStreamHandler.stop();
            }
        } else {
            smallViewBox.getChildren().clear();
            createStream(liveStreamHandler.getStreamLink(), liveStreamHandler, smallViewBox);
            bigViewBox.getChildren().clear();
            if (nextShot != null) {
                createStream(nextShot.getCamera().getConnection().getStreamLink(), nextStreamHandler, bigViewBox);
            } else {
                initializeBlackViews(true);
                nextStreamHandler.stop();
            }
        }      
    }
    
    /**
     * Creates a livestream in a VBox.
     * @param streamLink The link of the livestream.
     * @param streamHandler The LiveStreamHandler responsible for the stream.
     * @param viewBox The VBox which will contain the stream.
     */
    private void createStream(String streamLink, LiveStreamHandler streamHandler, VBox viewBox) {
        if (streamHandler != null) {
            streamHandler.stop();
        }

        viewBox.getChildren().clear();
        ImageView imgView = streamHandler.createImageView(streamLink, 1920, 1080);
        viewBox.getChildren().add(imgView);
        Platform.runLater(() -> {
            fitImageViewSize((float) viewBox.getWidth(), (float) viewBox.getHeight(), imgView, streamHandler);
        });       
        streamHandler.start();
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
     * Calling this method shows this view in the middle of the rootLayout,
     * forcing the current view to disappear.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/DirectorLiveView.fxml"));
            AnchorPane cameraLiveUI = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(cameraLiveUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
