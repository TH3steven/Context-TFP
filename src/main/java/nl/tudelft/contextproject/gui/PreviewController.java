package main.java.nl.tudelft.contextproject.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import main.java.nl.tudelft.contextproject.ContextTFP;

import java.io.IOException;

/**
 * Controller class for the script creation screen.
 * 
 * @author Tim Yue
 */
public class PreviewController {
    @FXML HBox shotBox;
    @FXML ImageView actualCam;
    @FXML ImageView imgOne;
    @FXML ImageView imgTwo;
    @FXML ImageView imgThree;
    @FXML ImageView imgFour;
    @FXML Button play;
    @FXML Button stop;
    @FXML Button back;
    @FXML ChoiceBox<String> cb;
    @FXML TextField duration;
    private int currentImg;
    private Timeline timeline;
    
    @FXML
    private void initialize() {
        actualCam.setImage(new Image("main/resources/placeholder_picture.jpg"));
        imgOne.setImage(new Image("main/resources/placeholder_picture.jpg"));
        imgTwo.setImage(new Image("main/resources/test2.jpg"));
        imgThree.setImage(new Image("main/resources/test3.jpg"));
        imgFour.setImage(new Image("main/resources/test4.jpg"));
        currentImg = 1;
        addTextLimiter(duration, 2);
        timeline = new Timeline(new KeyFrame(Duration.millis(2000), ae -> nextImage()));
        play.setOnAction((event) -> {
                timeline.play();
            });
        stop.setOnAction((event) -> {
                timeline.stop();
            });
        back.setOnAction((event) -> {
                timeline.stop();
                MenuController.show();
            });
        imgOne.setOnMouseClicked((event) -> {
                actualCam.setImage(imgOne.getImage());
                currentImg = 1;
                timeline.stop();
            });
        imgTwo.setOnMouseClicked((event) -> {
                actualCam.setImage(imgTwo.getImage());
                currentImg = 2;
                timeline.stop();
            });
        imgThree.setOnMouseClicked((event) -> {
                actualCam.setImage(imgThree.getImage());
                currentImg = 3;
                timeline.stop();
            });
        imgFour.setOnMouseClicked((event) -> {
                actualCam.setImage(imgFour.getImage());
                currentImg = 4;
                timeline.stop();
            });
        cb.setItems(FXCollections.observableArrayList("Shot 1", "Shot 2", "Shot 3", "Shot 4"));
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/ScriptPreview.fxml"));
            AnchorPane createScriptView = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(createScriptView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * shows the next image on the actual camera screen.
     */
    public void nextImage() {
        if (currentImg == 1) {
            actualCam.setImage(imgTwo.getImage());
            currentImg++;
            timeline.getKeyFrames().clear();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(5000), ae -> nextImage()));
            timeline.playFromStart();
        } else if (currentImg == 2) {
            actualCam.setImage(imgThree.getImage());
            currentImg++;
            timeline.playFromStart();
        } else if (currentImg == 3) {
            actualCam.setImage(imgFour.getImage());
            currentImg++;
            timeline.playFromStart();
        }
    }
    
    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }
}
