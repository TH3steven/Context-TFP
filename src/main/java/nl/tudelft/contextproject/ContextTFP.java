/**
 * This product is licensed under the CC BY 4.0 license and
 * may be modified without notice of the original creators. This modified version may
 * be distributed for commercial use when appropriate credit is given where due.
 * 
 * <p>See http://creativecommons.org/licenses/by/4.0/
 */

package nl.tudelft.contextproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import nl.tudelft.contextproject.gui.AlertDialog;
import nl.tudelft.contextproject.gui.MenuController;
import nl.tudelft.contextproject.saveLoad.ApplicationSettings;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This is the main file for the Multimedia Contextproject of Team Free Pizza.
 * The main purpose of this project is to allow PolyCast Productions B.V. to
 * easily digitize their script and to improve their digital environment.
 * 
 * <p>This file should be used to initialize the program.
 * 
 * @author Team Free Pizza
 * @version %I%, %G%
 * @since 0.1
 */
public class ContextTFP extends Application {

    private static boolean hasVLC;
    private static BorderPane rootLayout;
    private static Script script;

    private Stage primaryStage;

    @Override
    public void start(Stage pStage) throws Exception {
        primaryStage = pStage;
        primaryStage.setTitle("TFP Camera Control");
        primaryStage.minWidthProperty().set(800);
        primaryStage.minHeightProperty().set(575);
        primaryStage.getIcons().add(new Image(ContextTFP.class.getResourceAsStream("/icon.png")));

        // Create the script to be used by the application.
        script = new Script(new ArrayList<Shot>());

        // Statically initialize ApplicationSettings class.
        Path path = Paths.get("scr/main/resources/snapShot");
        if(Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            ApplicationSettings.getInstance();
        } else {
            File dir = new File("src/main/resources/snapShot");
            dir.mkdir();
            ApplicationSettings.getInstance();
        }

        initRootLayout();

        new Thread(() -> initVLCj()).start();

        MenuController.show();
    }

    /**
     * Initializes the root layout of the application.
     */
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e -> {
                try {
                    ApplicationSettings.getInstance().save();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Platform.exit(); 
                System.exit(0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to load VLC using the VLC native discovery tactics.
     * If it cannot find VLC installed, it will ask for the location of the
     * VLC installation.
     */
    public void initVLCj() {
        if (!new NativeDiscovery().discover()) {
            try {
                AlertDialog.errorVlcNotFound(primaryStage);
                hasVLC = true;
            } catch (RuntimeException e) {
                hasVLC = false;
            }
        } else {
            hasVLC = true;
        }
    }

    /**
     * The main class of the project. Calling this method will start the program.
     * @param args Environment arguments for the main method.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Get method for the primary stage of the application.
     * @return The main stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Get the active script used by the application.
     * @return The script.
     */
    public static Script getScript() {
        return script;
    }

    /**
     * Sets the active script used by the application.
     * @param script The script to be used.
     */
    public static void setScript(Script script) {
        ContextTFP.script = script;
    }

    /**
     * Retrieves the root layout of the application.
     * @return The root layout
     */
    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    /**
     * Checks of the user has VLC.
     * @return True if the program could find VLC.
     */
    public static boolean hasVLC() {
        return hasVLC;
    }
}
