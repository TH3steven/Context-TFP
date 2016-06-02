/**
 * This product is licensed under the CC BY 4.0 license and
 * may be modified without notice of the original creators. This modified version may
 * be distributed for commercial use when appropriate credit is given where due.
 * 
 * <p>See http://creativecommons.org/licenses/by/4.0/
 */

package nl.tudelft.contextproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.gui.MenuController;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the main file for the Multi-Media Contextproject of Team Free Pizza.
 * The main purpose of this project is to allow PolyCast Productions B.V. to
 * easily control their cameras and to improve their digital environment.
 * 
 * <p>This file should be used to initialize the program.
 * 
 * @author Team Free Pizza
 * @version %I%, %G%
 * @since 0.1
 */
public class ContextTFP extends Application {

    private static BorderPane rootLayout;
    private static Script script;

    private static Stage primaryStage;

    @Override
    public void start(Stage pStage) throws Exception {
        primaryStage = pStage;
        primaryStage.setTitle("TFP Camera Control");
        primaryStage.minWidthProperty().set(800);
        primaryStage.minHeightProperty().set(575);

        // Create the script to be used by the application.
        script = new Script(new ArrayList<Shot>());

        //TEMP
        Camera a = new Camera();
        Camera b = new Camera();
        Camera c = new Camera();
        Camera d = new Camera();
        Camera e = new Camera();
        Camera f = new Camera();
        
        List<Camera> list = new ArrayList<Camera>();
        list.addAll(Arrays.asList(a, b, c, d, e, f)); 

        for (Camera cam : list) {
            cam.addPreset(new InstantPreset(new CameraSettings(), 0, "wow"));
            cam.addPreset(new InstantPreset(new CameraSettings(), 1, "nice"));
            cam.addPreset(new InstantPreset(new CameraSettings(), 2, "awesome"));
            cam.addPreset(new InstantPreset(new CameraSettings(), 3, "wuq"));
        }
        //

        initRootLayout();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main class of the project. Calling this method will start the program.
     * 
     * @param args Environment arguments for the main method.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Get method for the primary stage of the application.
     * 
     * @return The main stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Get the active script used by the application.
     * 
     * @return The script.
     */
    public static Script getScript() {
        return script;
    }

    /**
     * Sets the active script used by the application.
     * 
     * @param script The script to be used.
     */
    public static void setScript(Script script) {
        ContextTFP.script = script;
    }

    /**
     * Retrieves the root layout of the application.
     * 
     * @return The root layout
     */
    public static BorderPane getRootLayout() {
        return rootLayout;
    }
}
