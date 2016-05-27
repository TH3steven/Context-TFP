package nl.tudelft.contextproject.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Shot;
import nl.tudelft.contextproject.script.Script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the script creation screen.
 * 
 * @since 0.2
 */
public class PreviewController {

    @FXML private Button play;
    @FXML private Button stop;
    @FXML private Button back;
    @FXML private Button confirm;
    @FXML private Button leftArrow;
    @FXML private Button rightArrow;

    @FXML private ChoiceBox<Shot> cb;
    
    @FXML private StackPane imagePane;

    @FXML private ImageView actualCam;
    
    @FXML private ImageView viewOne;
    @FXML private ImageView viewTwo;
    @FXML private ImageView viewThree;
    @FXML private ImageView viewFour;
    
    @FXML private Label camNum;
    @FXML private Label presNum;

    @FXML private Rectangle highlight1;
    @FXML private Rectangle highlight2;
    @FXML private Rectangle highlight3;
    @FXML private Rectangle highlight4;

    @FXML private TextArea descArea;
    @FXML private TextField duration;

    private SimpleIntegerProperty currentShot;
    private Shot currentFirst;
    private int currentHighlight;
    private Timeline timeline;
    private List<Shot> shots;
    private List<ImageView> views;
    private Script script;
    private List<Image> images;

    @FXML private void initialize() {

        script = ContextTFP.getScript();
        shots = script.getShots();
        
        currentShot = new SimpleIntegerProperty(1);
        currentHighlight = 1;

        initializeShots();
        initializeChoices();
        initializeImages();
        initializeActions();
        initializeListeners();
        initializeTextFields();
        if (!shots.isEmpty()) {
            initializeTimeline();
        }
        
        actualCam.fitWidthProperty().bind(imagePane.widthProperty());
        actualCam.fitHeightProperty().bind(imagePane.heightProperty());
        
        highlight1.setOpacity(0);
        highlight2.setOpacity(0);
        highlight3.setOpacity(0);
        highlight4.setOpacity(0);
    }

    /**
     * Initialize functionality on action.
     */
    private void initializeActions() {
        back.setOnAction((event) -> {
            MenuController.show();
        });
        
        int check = 0;
        if (shots.size() < views.size()) {
            check = shots.size();
        } else {
            check = views.size();
        }
        for (int i = 0; i < check; i++) {
            initializeViewActions(views.get(i), i + 1);
        }
        if (shots.size() > 0) {
            initializeShotActions();
        }
    }
    
    /**
     * Initialize actions which require shots to function.
     */
    private void initializeShotActions() {
        play.setOnAction((event) -> {
            timeline.playFromStart();
        });

        stop.setOnAction((event) -> {
            timeline.stop();
        });
        
        confirm.setOnAction((event) -> {
            Shot current = shots.get(currentShot.get() - 1);
            current.setDuration(Double.parseDouble(duration.getText()));
        });
        
        leftArrow.setOnAction((event) -> {
            int shotNumFirst = currentFirst.getNumber() - 1;
            if (shotNumFirst > 0) {
                switchViews(shots.get(shotNumFirst - 1), false);
                highlightRight();
            }
        });
        
        rightArrow.setOnAction((event) -> {
            int shotNumFirst = currentFirst.getNumber() - 1;
            if (shotNumFirst < shots.size() - 4) {
                switchViews(shots.get(shotNumFirst + 1), false);
                highlightLeft();
            }
        });
    }
    
    /**
     * Applies the standard settings to an ImageView.
     * @param view The ImageView to apply the setting to.
     * @param id The id of the ImageView.
     */
    private void initializeViewActions(ImageView view, int id) {
        view.setOnMouseClicked((event) -> {
            int diff = checkShots(shots.get(currentFirst.getNumber() - 1)) - id;
            int shotNum = currentFirst.getNumber() - diff;

            showShot(shots.get(shotNum - 1));

            timeline.stop();
        });
    }

    /**
     * Initialize the listeners.
     */
    private void initializeListeners() {
        final ChangeListener<Number> changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                cb.setValue(shots.get(currentShot.get() - 1));
                Shot shot = shots.get(currentShot.get() - 1);

                if (shot.getDuration() > 0) {
                    duration.setText(Double.toString(shots.get(currentShot.get() - 1).getDuration()));
                } else {
                    duration.setText("");
                }

                double shotDuration = shots.get(currentShot.get() - 1).getDuration();
                if (shotDuration > 0) {
                    timeline.getKeyFrames().clear();
                    timeline.getKeyFrames().add(new KeyFrame(
                            Duration.millis(shots.get(currentShot.get() - 1).getDuration() * 1000), ae -> nextImage()));
                } else {
                    timeline.getKeyFrames().add(new KeyFrame(
                            Duration.millis(1000), ae -> nextImage()));
                }
                timeline.stop();
            }
        };

        currentShot.addListener(changeListener);

        duration.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
                if (!newValue && !confirm.focusedProperty().get()) {
                    double shotDuration = shots.get(currentShot.get() - 1).getDuration();
                    
                    if (shotDuration > 0) {
                        duration.setText(Double.toString(shotDuration));
                    } else {
                        duration.setText("");
                    }
                }
            }
        });

        cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Shot>() {
            public void changed(ObservableValue<? extends Shot> ov, Shot oldVal, Shot newVal) {
                showShot(newVal);
                if (cb.getValue() == null) {
                    duration.setDisable(true);
                } else {
                    duration.setDisable(false);
                }
            }
        });
    }

    /**
     * Initialize the textfields.
     */
    private void initializeTextFields() {
        duration.setDisable(true);

        duration.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String character = event.getCharacter();

                if (!character.matches("[0-9.]")) {
                    event.consume();
                } else if (character.equals(".")) {
                    if (duration.getText().contains(".")) {
                        event.consume();
                    }
                } else if (duration.getText().length() == 5) {
                    event.consume();
                }
            }
        });
    }

    /**
     * Initialize the ImageViews with their images.
     */
    private void initializeImages() {
        actualCam.setImage(new Image("placeholder_picture.jpg"));
        
        views = new ArrayList<ImageView>();
        views.add(viewOne);
        views.add(viewTwo);
        views.add(viewThree);
        views.add(viewFour);
        
        for (int i = 0; i < 4; i++) {
            views.get(i).setImage(createInitialImage(i));
        }
    }

    /**
     * Make the dummy shots.
     */
    private void initializeShots() {       
        //add example images
        for (int i = 0; i < shots.size(); i++) {
            shots.get(i).getPreset().setImageLocation(getImageLoc(i));
            checkPresetImages(i);
        }
        if (!shots.isEmpty()) {
            currentFirst = shots.get(0);
        }
    }    

    /**
     * Fills the the choicebox with shots the user can choose from.
     */
    private void initializeChoices() {
        ObservableList<Shot> choices = FXCollections.observableArrayList();
        for (int i = 0; i < shots.size(); i++) {
            choices.add(shots.get(i));
        }
        cb.setItems(choices);
    }

    /**
     * Initialize the timeline with the duration of the first 
     * shot or 1 second by default.
     */
    private void initializeTimeline() {
        double initialDuration = shots.get(0).getDuration();
        if (initialDuration > 0) {
            timeline = new Timeline(new KeyFrame(Duration.millis(initialDuration * 1000), ae -> nextImage()));
        } else {
            timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> nextImage()));
        }
    }

    /**
     * Checks which shot in the shotlist is being viewed.
     * @return - the shotNumber in the shotlist
     */
    private int checkShots(Shot shot) {
        int returnValue = -1;
        
        if (shot.getNumber() <= shots.size() - 3) {
            returnValue = 1;
        } else if (shot.getNumber() == shots.size() - 2) {
            returnValue = 2;
        } else if (shot.getNumber() == shots.size() - 1) {
            returnValue = 3;
        } else if (shot.getNumber() == shots.size()) {
            returnValue = 4;
        } 
        
        return returnValue;
    }
    
    private void checkPresetImages(int i) {
        Preset preset = shots.get(i).getPreset();
        try {
            new Image(preset.getImage());
        } catch (IllegalArgumentException e) {
            preset.setImageLocation("error.jpg");
        }
    }
    
    /**
     * gets an image location as an example for each dummy shot.
     * @param i - number of the shot.
     * @return - image location.
     */
    private String getImageLoc(int i) {
        int check = i % 6;
        switch (check) {
            case 0:
                return "placeholder.jpg";
            case 1:
                return "test2.jpg";
            case 2:
                return "test3.jpg";
            case 3:
                return "test4.jpg";
            case 4:
                return "test5.jpg";
            case 5:
                return "test.jpg";
            default: 
                return "error.jpg";
        }
    }
    
    /**
     * Creates initial preview images for the shotbox.
     */
    private Image createInitialImage(int i) {
        try {
            return new Image(shots.get(i).getPreset().getImage());
        } catch (IndexOutOfBoundsException e) {
            return new Image("gray.jpg");
        }
    }
    
    /**
     * Shows the next image on the actual camera screen.
     */
    private void nextImage() {
        if (currentShot.get() == shots.size()) {
            timeline.stop();
            return;
        }

        showShot(shots.get(currentShot.get()));
        timeline.playFromStart();
    }

    /**
     * Shows the shot that is selected.
     * @param shot - The shot to be showed
     */
    private void showShot(Shot shot) {
        currentShot.set(shot.getNumber());
        actualCam.setImage(new Image(shot.getPreset().getImage()));
        switchViews(shot, true);
        switchInfo(shot);
    }
    
    private void switchViews(Shot shot, boolean shotSwitch) {
        int check = checkShots(shot);
        
        switch (check) {
            case 1: 
                viewOne.setImage(new Image(shot.getPreset().getImage()));
                viewTwo.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
                viewThree.setImage(new Image(shots.get(shot.getNumber() + 1).getPreset().getImage()));
                viewFour.setImage(new Image(shots.get(shot.getNumber() + 2).getPreset().getImage()));
                currentFirst = shot;
                if (shotSwitch) {
                    switchHighlights(1);
                }
                break;
            case 2: 
                viewOne.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
                viewTwo.setImage(new Image(shot.getPreset().getImage()));
                viewThree.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
                viewFour.setImage(new Image(shots.get(shot.getNumber() + 1).getPreset().getImage()));
                currentFirst = shots.get(shot.getNumber() - 2);
                if (shotSwitch) {
                    switchHighlights(2);
                }
                break;
            case 3: 
                viewOne.setImage(new Image(shots.get(shot.getNumber() - 3).getPreset().getImage()));
                viewTwo.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
                viewThree.setImage(new Image(shot.getPreset().getImage()));
                viewFour.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
                currentFirst = shots.get(shot.getNumber() - 3);
                if (shotSwitch) {
                    switchHighlights(3);
                }
                break;
            case 4: 
                viewOne.setImage(new Image(shots.get(shot.getNumber() - 4).getPreset().getImage()));
                viewTwo.setImage(new Image(shots.get(shot.getNumber() - 3).getPreset().getImage()));
                viewThree.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
                viewFour.setImage(new Image(shot.getPreset().getImage()));
                currentFirst = shots.get(shot.getNumber() - 4);
                if (shotSwitch) {
                    switchHighlights(4);
                }
                break;
            default: return;
        }
    }
    
    /**
     * Switch info of the shot info box to the corresponding selected shot.
     */
    private void switchInfo(Shot shot) {
        camNum.setText(String.valueOf(shot.getCamera().getNumber()));
        presNum.setText(String.valueOf(shot.getPreset().getId()));
        descArea.setText(shot.getDescription());
    }
    
    /**
     * Switches the highlight box.
     */
    private void switchHighlights(int id) {
        disableHighlight();
        currentHighlight = id;
        
        switch (id) {
            case 1: highlight1.setOpacity(1); break;
            case 2: highlight2.setOpacity(1); break;
            case 3: highlight3.setOpacity(1); break;
            case 4: highlight4.setOpacity(1); break;
            default: break;
        }
    }
    
    /**
     * Moves the highlightbox one shot to the right.
     */
    private void highlightRight() {
        currentHighlight++;
        if (highlightCheck()) {
            switchHighlights(currentHighlight);
        } else {
            disableHighlight();
        }
    }
    
    /**
     * Moves the highlightbox one shot to the left.
     */
    private void highlightLeft() {
        currentHighlight--;
        if (highlightCheck()) {
            switchHighlights(currentHighlight);
        } else {
            disableHighlight();
        }
    }
    
    /**
     * Checks if the current view to be highlighted is in bounds.
     */
    private boolean highlightCheck() {
        if (currentHighlight < 1 || currentHighlight > 4) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Sets the highlight box invisible.
     */
    private void disableHighlight() {
        highlight1.setOpacity(0);
        highlight2.setOpacity(0);
        highlight3.setOpacity(0);
        highlight4.setOpacity(0);
    }
    
    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/ScriptPreview.fxml"));
            AnchorPane scriptPreview = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(scriptPreview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
