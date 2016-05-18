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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.script.Shot;

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

    @FXML private ChoiceBox<Shot> cb;

    @FXML private ImageView actualCam;
    @FXML private ImageView viewOne;
    @FXML private ImageView viewTwo;
    @FXML private ImageView viewThree;
    @FXML private ImageView viewFour;

    @FXML private Rectangle highlight1;
    @FXML private Rectangle highlight2;
    @FXML private Rectangle highlight3;
    @FXML private Rectangle highlight4;

    @FXML private TextField duration;

    private SimpleIntegerProperty currentShot;
    private int currentSelected;
    private Timeline timeline;
    private List<Shot> shots;
    private List<ImageView> views;

    @FXML private void initialize() {
        currentSelected = 1;
        currentShot = new SimpleIntegerProperty(1);

        initializeShots();
        initializeChoices();
        initializeActions();
        initializeListeners();
        initializeTextFields();
        initializeImages();
        initializeTimeline();

        highlight2.setOpacity(0);
        highlight3.setOpacity(0);
        highlight4.setOpacity(0);
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
     * Initialize functionality on action.
     */
    private void initializeActions() {
        play.setOnAction((event) -> {
            timeline.playFromStart();
        });

        stop.setOnAction((event) -> {
            timeline.stop();
        });

        back.setOnAction((event) -> {
            timeline.stop();
            MenuController.show();
        });

        confirm.setOnAction((event) -> {
            Shot current = shots.get(currentShot.get() - 1);
            current.setDuration(Double.parseDouble(duration.getText()));
        });

        viewOne.setOnMouseClicked((event) -> {
            int diff = currentSelected - 1;
            int shotNum = currentShot.get() - diff;

            showShot(shots.get(shotNum - 1));
            highlight1.setOpacity(1);
            highlight2.setOpacity(0);
            highlight3.setOpacity(0);
            highlight4.setOpacity(0);
            timeline.stop();
        });

        viewTwo.setOnMouseClicked((event) -> {
            int diff = currentSelected - 2;
            int shotNum = currentShot.get() - diff;

            showShot(shots.get(shotNum - 1));
            highlight1.setOpacity(0);
            highlight2.setOpacity(1);
            highlight3.setOpacity(0);
            highlight4.setOpacity(0);
            timeline.stop();
        });

        viewThree.setOnMouseClicked((event) -> {
            int diff = currentSelected - 3;
            int shotNum = currentShot.get() - diff;

            showShot(shots.get(shotNum - 1));
            highlight1.setOpacity(0);
            highlight2.setOpacity(0);
            highlight3.setOpacity(1);
            highlight4.setOpacity(0);
            timeline.stop();
        });

        viewFour.setOnMouseClicked((event) -> {
            int diff = currentSelected - 4;
            int shotNum = currentShot.get() - diff;

            showShot(shots.get(shotNum - 1));
            highlight1.setOpacity(0);
            highlight2.setOpacity(0);
            highlight3.setOpacity(0);
            highlight4.setOpacity(1);
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
        Image imgOne = new Image("placeholder_picture.jpg");
        Image imgTwo = new Image("test2.jpg");
        Image imgThree = new Image("test3.jpg");
        Image imgFour = new Image("test4.jpg");

        viewOne.setImage(imgOne);
        viewTwo.setImage(imgTwo);
        viewThree.setImage(imgThree);
        viewFour.setImage(imgFour);

        views = new ArrayList<ImageView>();
        views.add(viewOne);
        views.add(viewTwo);
        views.add(viewThree);
        views.add(viewFour);
    }

    /**
     * Make the dummy shots.
     */
    private void initializeShots() {
        CameraSettings dummySettings = new CameraSettings();
        Camera dummyCamera = new Camera();

        InstantPreset presetOne = new InstantPreset(dummySettings, 1);
        InstantPreset presetTwo = new InstantPreset(dummySettings, 2);
        InstantPreset presetThree = new InstantPreset(dummySettings, 3);
        InstantPreset presetFour = new InstantPreset(dummySettings, 4);
        InstantPreset presetFive = new InstantPreset(dummySettings, 5);
        InstantPreset presetSix = new InstantPreset(dummySettings, 6);

        presetOne.setImageLocation("placeholder_picture.jpg");
        presetTwo.setImageLocation("test2.jpg");
        presetThree.setImageLocation("test3.jpg");
        presetFour.setImageLocation("test4.jpg");
        presetFive.setImageLocation("test5.jpg");
        presetSix.setImageLocation("test6.jpg");

        Shot shotOne = new Shot(1, dummyCamera, presetOne);
        Shot shotTwo = new Shot(2, dummyCamera, presetTwo);
        Shot shotThree = new Shot(3, dummyCamera, presetThree);
        Shot shotFour = new Shot(4, dummyCamera, presetFour);
        Shot shotFive = new Shot(5, dummyCamera, presetFive);
        Shot shotSix = new Shot(6, dummyCamera, presetSix);

        shots = new ArrayList<Shot>();
        shots.add(shotOne);
        shots.add(shotTwo);
        shots.add(shotThree);
        shots.add(shotFour);
        shots.add(shotFive);
        shots.add(shotSix);
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
    private int checkShots() {
        int returnValue = -1;
        
        if (currentShot.get() <= shots.size() - 3) {
            currentSelected = 1;
            returnValue = 0;
        } else if (currentShot.get() == shots.size() - 2) {
            currentSelected = 2;
            returnValue = 1;
        } else if (currentShot.get() == shots.size() - 1) {
            currentSelected = 3;
            returnValue = 2;
        } else if (currentShot.get() == shots.size()) {
            currentSelected = 4;
            returnValue = 3;
        } 
        
        return returnValue;
    }

    /**
     * Shows the shot that is selected.
     * @param shot - The shot to be showed
     */
    private void showShot(Shot shot) {
        currentShot.set(shot.getNumber());
        int check = checkShots();

        switch (check) {
            case 0: 
                actualCam.setImage(new Image(shot.getPreset().getImage()));
                viewOne.setImage(new Image(shot.getPreset().getImage()));
                viewTwo.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
                viewThree.setImage(new Image(shots.get(shot.getNumber() + 1).getPreset().getImage()));
                viewFour.setImage(new Image(shots.get(shot.getNumber() + 2).getPreset().getImage()));
                break;
            case 1: 
                actualCam.setImage(new Image(shot.getPreset().getImage()));
                viewOne.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
                viewTwo.setImage(new Image(shot.getPreset().getImage()));
                viewThree.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
                viewFour.setImage(new Image(shots.get(shot.getNumber() + 1).getPreset().getImage()));
                break;
            case 2: 
                actualCam.setImage(new Image(shot.getPreset().getImage()));
                viewOne.setImage(new Image(shots.get(shot.getNumber() - 3).getPreset().getImage()));
                viewTwo.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
                viewThree.setImage(new Image(shot.getPreset().getImage()));
                viewFour.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
                break;
            case 3: 
                actualCam.setImage(new Image(shot.getPreset().getImage()));
                viewOne.setImage(new Image(shots.get(shot.getNumber() - 4).getPreset().getImage()));
                viewTwo.setImage(new Image(shots.get(shot.getNumber() - 3).getPreset().getImage()));
                viewThree.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
                viewFour.setImage(new Image(shot.getPreset().getImage()));
                break;
            default: return;
        }

        if (checkShots() == 0) {
            actualCam.setImage(new Image(shot.getPreset().getImage()));
            viewOne.setImage(new Image(shot.getPreset().getImage()));
            viewTwo.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
            viewThree.setImage(new Image(shots.get(shot.getNumber() + 1).getPreset().getImage()));
            viewFour.setImage(new Image(shots.get(shot.getNumber() + 2).getPreset().getImage()));
        } else if (checkShots() == 1) {
            actualCam.setImage(new Image(shot.getPreset().getImage()));
            viewOne.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
            viewTwo.setImage(new Image(shot.getPreset().getImage()));
            viewThree.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
            viewFour.setImage(new Image(shots.get(shot.getNumber() + 1).getPreset().getImage()));
        } else if (checkShots() == 2) {
            actualCam.setImage(new Image(shot.getPreset().getImage()));
            viewOne.setImage(new Image(shots.get(shot.getNumber() - 3).getPreset().getImage()));
            viewTwo.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
            viewThree.setImage(new Image(shot.getPreset().getImage()));
            viewFour.setImage(new Image(shots.get(shot.getNumber()).getPreset().getImage()));
        } else if (checkShots() == 3) {
            actualCam.setImage(new Image(shot.getPreset().getImage()));
            viewOne.setImage(new Image(shots.get(shot.getNumber() - 4).getPreset().getImage()));
            viewTwo.setImage(new Image(shots.get(shot.getNumber() - 3).getPreset().getImage()));
            viewThree.setImage(new Image(shots.get(shot.getNumber() - 2).getPreset().getImage()));
            viewFour.setImage(new Image(shot.getPreset().getImage()));
        }
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
