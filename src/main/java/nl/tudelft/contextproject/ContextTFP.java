/**
 * This product is licensed under the CC BY 4.0 license and
 * may be modified without notice of the original creators. This modified version may
 * be distributed for commercial use when appropriate credit is given where due.
 * 
 * See http://creativecommons.org/licenses/by/4.0/
 */

package main.java.nl.tudelft.contextproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import main.java.nl.tudelft.contextproject.gui.MenuController;
import main.java.nl.tudelft.contextproject.script.Script;
import main.java.nl.tudelft.contextproject.script.Shot;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the main file for the contextproject of Team Free Pizza.
 * The main purpose of this project is to allow PolyCast Productions B.V. to
 * easily control their cameras and to improve their digital environment.
 * 
 * <p>This file shoud be used to initialize the program.
 * 
 * @author Team Free Pizza
 * @version %I%, %G%
 * @since 0.1
 */
public class ContextTFP extends Application {

    private static BorderPane rootLayout;
    private static Script script;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TFP Camera Control");

        script = new Script(new ArrayList<Shot>());
        
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
    public Stage getPrimaryStage() {
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
    
    public void setScript(Script script) {
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
