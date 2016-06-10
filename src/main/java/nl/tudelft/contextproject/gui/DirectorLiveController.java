package nl.tudelft.contextproject.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.List;
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
public class DirectorLiveController /*implements Observer*/ {

    private static Script script;

    @FXML private Button btnBack;
    @FXML private Button btnNext;
    @FXML private Button btnSwap;
    
    @FXML private CheckBox automaticCheck;

    @FXML private ImageView bigView;
    @FXML private ImageView smallView;

    @FXML private Label bigCameraNumberLabel;
    @FXML private Label bigShotNumberLabel;
    @FXML private Label bigPresetLabel;
    @FXML private Label bigStatusLabel;
    @FXML private Label smallCameraNumberLabel;
    @FXML private Label smallPresetLabel;
    @FXML private Label smallShotNumberLabel;
    @FXML private Label smallStatusLabel;
    
    @FXML private TableView test;
    public static final ObservableList names = 
            FXCollections.observableArrayList();

    @FXML private TextArea bigDescriptionField;
    @FXML private TextArea smallDescriptionField;

    @FXML private VBox bigViewBox;
    @FXML private VBox smallViewBox;
    @FXML private VBox vboxButtons;
    @FXML private VBox vboxLabels;
    
    private boolean endReached;
    private boolean live;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        script = ContextTFP.getScript();
        
        endReached = false;
        live = true;
        
        names.addAll(new Label("test"), new Label("test"), new Label("test"), new Label("test"), new Label("test"), new Label("test"), new Label("test"), new Label("test"), new Label("test"));
        test.setItems(names);
        bigView.fitWidthProperty().bind(bigViewBox.widthProperty());
        bigView.fitHeightProperty().bind(bigViewBox.heightProperty());

        smallView.fitWidthProperty().bind(smallViewBox.widthProperty());
        smallView.fitHeightProperty().bind(smallViewBox.heightProperty());

        initializeLabels();
        initializeViews();
        initializeButtons();        
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
     * Initializes the views.
     */
    private void initializeViews() {
        Image actual = new Image("placeholder_picture.jpg");
        bigView.setImage(actual);
        
        Image next;
        if (script.getNextShot() != null) {
            next = new Image("test3.jpg");
        } else {
            next = new Image("black.png");
        }
        smallView.setImage(next);
    }

    /**
     * Initializes the swap and back onAction events.
     */
    private void initializeButtons() {
        btnSwap.setOnAction((event) -> {
            Image three = bigView.getImage();
            bigView.setImage(smallView.getImage());
            smallView.setImage(three);
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
            
            live = !live;
            
            updateTables();
        });

        btnBack.toFront();
        btnBack.setOnAction((event) -> {
            MenuController.show();
        });
        
        btnNext.setOnAction((event) -> {
            if (!endReached) {
                if (automaticCheck.isSelected()) {
                    script.next(true);
                } else {
                    script.next(false);
//                    updateExecuteButtons();
                }
                updateTables();
            }
        });
    }

    /**
     * Updates the table contents according to the current position in the script.
     */
    private void updateTables() {
        Shot smallShot;
        Shot bigShot;
        if (live) {
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
            endOfScript(false);
        }

        if (bigShot != null) {
            bigShotNumberLabel.setText(bigShot.getShotId());
            bigCameraNumberLabel.setText(Integer.toString(bigShot.getCamera().getNumber()));
            bigPresetLabel.setText(Integer.toString(bigShot.getPreset().getId()));
            bigDescriptionField.setText(bigShot.getDescription());
        } else {
            endOfScript(true);
        }
    }
    
    private void endOfScript(boolean big) {
        if (big) {
            bigView.setImage(new Image("black.png"));
            bigShotNumberLabel.setText("");
            bigCameraNumberLabel.setText("");
            bigPresetLabel.setText("");
            bigDescriptionField.setText("End of script reached");
            endReached = true;
        } else {
            smallView.setImage(new Image("black.png"));
            smallShotNumberLabel.setText("");
            smallCameraNumberLabel.setText("");
            smallPresetLabel.setText("");
            smallDescriptionField.setText("End of script reached");
            endReached = true;
        }
    }
    
//    private void updateExecuteButtons() {
//        for (int i = 0; i < script.getSkippedShots().size()) {
//            
//        }
//    }
    
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

//    @Override
//    public void update(Observable o, Object arg) {
//        List<Shot> skipped = script.getSkippedShots();
//        <vboxLabels>.clear();
//        <vboxButtons>.clear();
//        
//        for (Shot sk : skipped) {
//            Label label = new Label(sk.toString());
//            Button execute = new Button("Execute");
//            
//            execute.setOnAction((event) -> {
//                script.removeSkippedShot(sk);
//            });
//            
//            vboxLabels.add(label);
//            vboxButtons.add(execute);
//        }
//    }
}
