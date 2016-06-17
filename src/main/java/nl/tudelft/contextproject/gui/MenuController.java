package nl.tudelft.contextproject.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import nl.tudelft.contextproject.ContextTFP;
import nl.tudelft.contextproject.saveLoad.LoadScript;
import nl.tudelft.contextproject.saveLoad.SaveScript;
import nl.tudelft.contextproject.script.Script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller class for the main menu. This class controls the actions to be taken
 * when one of the menu buttons is clicked. Additionally, this class is responsible
 * for the displaying of the logo and the label that indicates the active {@link Script}.
 * 
 * @since 0.1
 */
public class MenuController {

    @FXML private Button btnCameraman;
    @FXML private Button btnCameras;
    @FXML private Button btnCreateScript;
    @FXML private Button btnDirector;
    @FXML private Button btnEditScript;
    @FXML private Button btnPre;
    @FXML private Button btnPresets;
    @FXML private Button btnPreview;
    @FXML private Button btnLive;
    @FXML private Button btnLoadScript;

    @FXML private ImageView settings;

    @FXML private Label lblPre;
    @FXML private Label lblLive;
    @FXML private Label lblVersion;
    @FXML private Label lblScript;
    
    private List<Node> preNodes;
    private List<Node> liveNodes;
    
    private boolean isPreVisible = false;
    private boolean isLiveVisible = false;

    /**
     * Initialize method used by JavaFX.
     */
    @FXML private void initialize() {
        final String name = ContextTFP.getScript().getName();

        if (name.equals("")) {
            setScriptLabel("None");
        } else {
            setScriptLabel(name);
        }

        setVersionLabel("0.7");
        
        preNodes = new ArrayList<Node>();
        liveNodes = new ArrayList<Node>();
        preNodes.addAll(Arrays.asList(lblPre, btnCreateScript, 
                btnEditScript, btnPresets, btnPreview, btnLoadScript));
        liveNodes.addAll(Arrays.asList(lblLive, btnCameraman, btnDirector));

        initLoadButton();
        initSubButtons();
        initOtherButtons();
        initSettingsImg();
        initCameraViewButton();
    }

    private void initLoadButton() {
        btnLoadScript.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select script to use");
            fileChooser.getExtensionFilters().add(new ExtensionFilter("XML (*.xml)", "*.xml"));

            File file = fileChooser.showOpenDialog(((Node) event.getTarget()).getScene().getWindow());

            if (file != null) {
                try {
                    LoadScript.setLoadLocation(file.getAbsolutePath());
                    SaveScript.setSaveLocation(file.getAbsolutePath());

                    ContextTFP.setScript(LoadScript.load());
                    ContextTFP.getScript().setName(file.getName());
                    setScriptLabel(file.getName());

                    AlertDialog.infoSuccesfulLoading(file);
                    CreateScriptController.showValid(ContextTFP.getScript(), 2);
                } catch (Exception e) {
                    AlertDialog.errorSaveUnsuccesful(e, file);
                }
            }
        });
    }
    
    /**
     * Initializes the onAction events for the buttons that display
     * the sub menus.
     */
    private void initSubButtons() {
        btnPre.setOnAction(event -> {
            animate(true);

            isPreVisible = !isPreVisible;
            isLiveVisible = false;
            
            toggleNodeVisibility(preNodes, liveNodes);
        });
        
        btnLive.setOnAction(event -> {
            animate(false);
            
            isLiveVisible = !isLiveVisible;
            isPreVisible = false;
            
            toggleNodeVisibility(liveNodes, preNodes);
        });
    }
    
    /**
     * Animates the sub buttons up or down.
     * 
     * @param pre Should be true if the pre button was clicked,
     *      false if the live button was clicked.
     */
    private void animate(boolean pre) {
        if (!isPreVisible && !isLiveVisible) {
            Animation.animNodeUp(btnPre);
            Animation.animNodeUp(btnLive);
        } else if (isPreVisible && !isLiveVisible && pre) {
            Animation.animNodeDown(btnPre);
            Animation.animNodeDown(btnLive);
        } else if (!isPreVisible && isLiveVisible && !pre) {
            Animation.animNodeDown(btnPre);
            Animation.animNodeDown(btnLive);
        }
    }
    
    /**
     * Toggles the visibility of the nodes of the sub menu.
     * 
     * @param clicked The nodes of the button the user clicked on.
     * @param hidden The nodes of the button the user did not click on.
     */
    private void toggleNodeVisibility(List<Node> clicked, List<Node> hidden) {
        for (Node b : clicked) {
            if (b.isVisible()) {
                Animation.animNodeOut(b, true);
            } else {
                Animation.animNodeIn(b);
            }
        }
        
        for (Node b : hidden) {
            if (b.isVisible()) {
                Animation.animNodeOut(b, true);
            }
        }
    }

    /**
     * Initializes the rest of the buttons.
     */
    private void initOtherButtons() {
        btnCreateScript.setOnAction(event -> {
            Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                CreateScriptController.show();
                Animation.animNodeIn(ContextTFP.getRootLayout());
            });
        });

        btnPreview.setOnAction(event -> {
            Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                PreviewController.show();
                Animation.animNodeIn(ContextTFP.getRootLayout());
            });
        });

        btnPresets.setOnAction(event -> {
            Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                PresetController.show();
                Animation.animNodeIn(ContextTFP.getRootLayout());
            });
        });

        btnDirector.setOnAction(event -> {
            Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                DirectorLiveController.show();
                Animation.animNodeIn(ContextTFP.getRootLayout());
            });
        });

        btnCameraman.setOnAction(event -> {
            Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                CameramanLiveController.show();
                Animation.animNodeIn(ContextTFP.getRootLayout());
            });
        });

        btnEditScript.setOnAction(event -> {
            Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                CreateScriptController.setFill(true);
                CreateScriptController.show();
                Animation.animNodeIn(ContextTFP.getRootLayout());
            });
        });
    }

    /**
     * Sets the hover and click action for the settings icon.
     */
    private void initSettingsImg() {
        settings.setOnMouseEntered(event -> {
            settings.setImage(new Image("settings_active.png"));
        });
        
        settings.setOnMouseExited(event -> {
            settings.setImage(new Image("settings.png"));
        });
    }
    
    private void initCameraViewButton() {
        btnCameras.setOnAction(event -> {
            Stage secondaryStage = new Stage();
            secondaryStage.show();
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(ContextTFP.class.getResource("view/CameraFeedsView.fxml"));

                AnchorPane rootLayout = (AnchorPane) loader.load();

                Scene scene = new Scene(rootLayout);
                secondaryStage.setScene(scene);
                secondaryStage.show();
                secondaryStage.setOnCloseRequest(e -> {
                    CameraFeedsController.closeStreams();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets the text of the script label on the menu.
     * 
     * @param text The text to set to the label. This will be appended to the
     *      string: "Current script: " 
     */
    private void setScriptLabel(String text) {
        lblScript.setText("Current script: " + text);
    }

    /**
     * Sets the text of the version label on the menu.
     * 
     * @param text The text to set to the label. This will be appended to the
     *      string: "Current version: " 
     */
    private void setVersionLabel(String text) {
        lblVersion.setText("Current version: " + text);
    }

    /**
     * Calling this method shows this view in the middle of the rootLayout,
     * forcing the current view to disappear.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/MenuOverview.fxml"));
            AnchorPane menuOverview = (AnchorPane) loader.load();

            Animation.animNodeOut(ContextTFP.getRootLayout(), false).setOnFinished(f -> {
                ContextTFP.getRootLayout().setCenter(menuOverview);
                Animation.animNodeIn(ContextTFP.getRootLayout());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
