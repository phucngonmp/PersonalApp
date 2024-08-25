package org.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {


    @FXML
    private VBox contentArea;


    @FXML
    private void loadToDoListLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/ToDoListLayout.fxml"));
            Pane toDoListLayout = loader.load();

            // Retrieve the ToDoListController
            ToDoListController toDoListController = loader.getController();

            FXMLLoader calendarLoader = new FXMLLoader(getClass().getResource("/org/example/demo/CalendarLayout.fxml"));
            Pane calendarLayout = calendarLoader.load();
            toDoListController.getRightPane().getChildren().add(calendarLayout);
            CalendarController calendarController = calendarLoader.getController();


            // Pass the ToDoListController to the CalendarController
            calendarController.setToDoListController(toDoListController);

            // Add the layout to the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(toDoListLayout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
