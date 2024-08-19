package org.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.demo.Main;

import java.io.IOException;

public class MainController {

    @FXML
    private VBox contentArea;

    @FXML
    private Button toDoListBtn;

    @FXML
    private void loadToDolistLayout(){
        loadLayout("/org/example/demo/ToDoListLayout.fxml");
    }

    @FXML
    private void loadTimerLayout(){
        loadLayout("/org/example/demo/TimerLayout.fxml");
    }


    private void loadLayout(String fxmlFile){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane newLayout = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(newLayout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
