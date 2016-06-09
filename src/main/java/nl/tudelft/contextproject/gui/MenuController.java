package nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

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
    @FXML private Button btnCreateScript;
    @FXML private Button btnDirector;
    @FXML private Button btnEditScript;
    @FXML private Button btnPre;
    @FXML private Button btnPresets;
    @FXML private Button btnPreview;
    @FXML private Button btnLive;
    @FXML private Button btnLoadScript;

    @FXML private Label lblVersion;
    @FXML private Label lblScript;
    
    private List<Button> preButtons;
    private List<Button> liveButtons;
    
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
        
        preButtons = new ArrayList<Button>();
        liveButtons = new ArrayList<Button>();
        preButtons.addAll(Arrays.asList(btnCreateScript, 
                btnEditScript, btnPresets, btnPreview, btnLoadScript));
        liveButtons.addAll(Arrays.asList(btnCameraman, btnDirector));

        initLoadButton();
        initOtherButtons();
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
     * Initializes the rest of the buttons.
     */
    private void initOtherButtons() {
        btnCreateScript.setOnAction(event -> {
            CreateScriptController.show();
        });

        btnPreview.setOnAction(event -> {
            PreviewController.show();
        });

        btnPresets.setOnAction(event -> {
            PresetController.show();
        });

        btnDirector.setOnAction(event -> {
            DirectorLiveController.show();
        });

        btnCameraman.setOnAction(event -> {
            CameramanLiveController.show();
        });

        btnEditScript.setOnAction(event -> {
            CreateScriptController.setFill(true);
            CreateScriptController.show();
        });
        
        btnPre.setOnAction(event -> {
            animate(true);
            
            toggleButtonVisibility(preButtons);
            isPreVisible = !isPreVisible;
        });
        
        btnLive.setOnAction(event -> {
            animate(false);
            
            toggleButtonVisibility(liveButtons);
            isLiveVisible = !isLiveVisible;
        });
    }
    
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
    
    private void toggleButtonVisibility(List<Button> list) {
        for (Button b : list) {
            if (b.isVisible()) {
                Animation.animNodeOut(b);
            } else {
                Animation.animNodeIn(b);
            }
        }
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

            ContextTFP.getRootLayout().setCenter(menuOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
