package main.java.nl.tudelft.contextproject.gui;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import main.java.nl.tudelft.contextproject.ContextTFP;

public class PresetController {
    
    @FXML private ChoiceBox<Integer> selectCamera;
    @FXML private ChoiceBox<Integer> presetID;
    @FXML private ImageView cameraView;
    @FXML private Button btnBack;
    
    @FXML
    private void initialize() {
        selectCamera.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8));
        presetID.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8));
        cameraView.setImage(new Image("main/resources/placeholder_picture.jpg"));
        btnBack.setOnAction((event) -> {
            MenuController.show();
        });
    }

    /**
     * Shows this view.
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ContextTFP.class.getResource("view/PresetView.fxml"));
            AnchorPane createScriptView = (AnchorPane) loader.load();

            ContextTFP.getRootLayout().setCenter(createScriptView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
