package nl.tudelft.contextproject.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    @FXML private Button btnPresets;
    @FXML private Button btnPreview;
    @FXML private Button btnLoadScript;

    @FXML private Label lblVersion;
    @FXML private Label lblScript;

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
        
        setVersionLabel("0.6");

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

                    showSuccessDialog(file);
                    ContextTFP.getScript().showValid(2);
                } catch (Exception e) {
                    showErrorDialog(e, file);
                }
            }
        });

        initOtherButtons();
    }

    /**
     * Shows the dialog that notifies the user that the loading
     * of a script was successful.
     * 
     * @param file The location of the script that was loaded.
     */
    private void showSuccessDialog(File file) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Info Dialog");
        alert.setHeaderText("Loading script was succesful!");
        alert.setContentText("Successful load of script: " + file.getName());

        alert.showAndWait();
    }

    /**
     * Displays an error dialog when saving of the script
     * was unsuccessful.
     * 
     * @param e The exception that was thrown.
     * @param file The file that was supposed to be saved.
     */
    private void showErrorDialog(Exception e, File file) {
        String c = (e.getCause() == null) ? "" : "\nCause: "  + e.getCause();
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Unsuccesful load!");
        alert.setHeaderText("Loading script was unsuccesful!");
        alert.setContentText("Error when trying to load script at location: " 
                + file.getAbsolutePath()
                + "\n\nError: "
                + e.getMessage()
                + c);

        alert.showAndWait();
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
