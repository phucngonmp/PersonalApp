package org.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
            CalendarController calendarController = new CalendarController();
            calendarLoader.setController(calendarController);
            Pane calendarLayout = calendarLoader.load();
            toDoListController.getRightPane().getChildren().add(calendarLayout);

            calendarController.setDayClickHandler(toDoListController);


            // Add the layout to the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(toDoListLayout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void loadDeadlineLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/DeadlineLayout.fxml"));
            Pane deadLineLayout = loader.load();

            // Retrieve the ToDoListController
            DeadlineController deadlineController = loader.getController();

            FXMLLoader calendarLoader = new FXMLLoader(getClass().getResource("/org/example/demo/CalendarLayout.fxml"));

            // create and set the deadlineCalendarController to this calendarLayout
            DeadlineCalendarController calendarController = new DeadlineCalendarController();
            calendarLoader.setController(calendarController);
            deadlineController.setCalendarController(calendarController);
            Pane calendarLayout = calendarLoader.load();

            // set deadlineController to handler of calendar controller
            calendarController.setDayClickHandler(deadlineController);



            // load calendar to top pane of deadline layout
            deadlineController.getAbovePane().getChildren().add(calendarLayout);


            // Add the layout to the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(deadLineLayout);
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
