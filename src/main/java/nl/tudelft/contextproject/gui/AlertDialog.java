package nl.tudelft.contextproject.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import nl.tudelft.contextproject.script.Shot;

import java.io.File;
import java.util.Optional;

/**
 * Utility class that holds all generated alert dialogs. This handles 
 * the creation and showing of info, warning and error dialogs 
 * across the entire application.
 *
 * @since 0.6
 */
public final class AlertDialog {

    /**
     * Abstract classes should not have a public constructor,
     * so defining it as private below.
     */
    private AlertDialog() { }

    /**
     * Shows a confirmation dialog when trying to save a {@link Script}, when
     * the script is invalid.
     * 
     * @param error The first shot that causes the invalid script.
     * @return The alert.
     */
    public static Alert confirmInvalidScriptSaving(Shot error) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm saving");
        alert.setHeaderText("Trying to save invalid script");
        alert.setContentText("Error at shot ID: " 
                + error.getNumber()
                + "\nYou are trying to save an invalid script. "
                + "Are you sure you want to continue?");

        return alert;
    }

    /**
     * Shows a warning dialog when trying to load a {@link Script}, when
     * the script is invalid.
     * 
     * @param error The first shot that causes the invalid script.
     * @return The alert.
     */
    public static Alert warningInvalidScriptLoading(Shot error) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Script invalid");
        alert.setHeaderText("Loaded an invalid script");
        alert.setContentText("Error at shot ID: " 
                + error.getNumber()
                + "\nThe script you loaded is invalid. You can change "
                + "it in the edit script menu");

        return alert;
    }

    /**
     * Displays a confirm to exit dialog when the 
     * script has been saved.
     * 
     * @param file The file that has been saved.
     */
    public static void confirmExitingAfterSaving(File file) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm exiting");
        alert.setHeaderText("Saving script was succesful!");
        alert.setContentText("Succesful save of script: " 
                + file.getName()
                + " Do you want to quit to menu?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            MenuController.show();
        }
    }

    /**
     * Shows the dialog when an invalid script is detected.
     * 
     * @param number The camera that will have a consecutive shot.
     * @return True if the user ignores the error, false otherwise.
     */
    public static boolean confirmInvalidScriptAdding(Number number) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm adding shot");
        alert.setHeaderText("Invalid script!");
        alert.setContentText("Are you sure you want to add this "
                + "shot? It will create an invalid script since "
                + "camera "
                + number
                + " will have two presets in a row!");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            return true;
        }

        return false;
    }

    /**
     * Displays an error dialog when saving of the script
     * was unsuccessful.
     * 
     * @param e The exception that was thrown.
     * @param file The file that was supposed to be saved.
     */
    public static void errorSaveUnsuccesful(Exception e, File file) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(e.getMessage());
        alert.setHeaderText("Saving script was unsuccesful!");
        alert.setContentText("Error when trying to save script at location: " 
                + file.getAbsolutePath()
                + "\n\nError: "
                + e.getCause());

        alert.showAndWait();
    }

    /**
     * Shows a dialog when exiting a screen without saving, thus
     * not saving currently made changes.
     * 
     * @return True if the user wants to quit, false otherwise.
     */
    public static boolean confirmExiting() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm quitting");
        alert.setHeaderText("Exiting will erase any unsaved changes");
        alert.setContentText("Are you sure you want to quit? Any unsaved changes "
                + "will not be kept.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.CANCEL) {
            return false;
        }

        return true;
    }
    
    /**
     * Shows the dialog that notifies the user that the loading
     * of a script was successful.
     * 
     * @param file The location of the script that was loaded.
     */
    public static void infoSuccesfulLoading(File file) {
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
    public static void errorLoadUnsuccesful(Exception e, File file) {
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
}
