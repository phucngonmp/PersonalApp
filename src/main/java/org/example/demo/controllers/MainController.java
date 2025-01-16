package org.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class MainController {


    @FXML
    private VBox contentArea;

    public boolean isTimerRunning = false;
    public boolean isTimerInProgress = false;


    @FXML
    private void loadToDoListLayout() {
        if(isTimerRunning){
            return;
        }
        if(isUserAcceptToSwitch()){
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
    }
    @FXML
    public void loadDeadlineLayout() {
        if(isTimerRunning){
            return;
        }
        if(isUserAcceptToSwitch()) {
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
    }

    @FXML
    private void loadTimerLayout() throws IOException {
        if(isTimerRunning){
            return;
        }
        if(isUserAcceptToSwitch()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/demo/TimerLayout.fxml"));
            Pane newLayout = loader.load();
            TimerController timerController = loader.getController();
            timerController.setMainController(this);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(newLayout);
        }
    }

    @FXML
    private void loadStatisticLayout(){
        if(isTimerRunning){
            return;
        }
        if(isUserAcceptToSwitch())
            loadLayout("/org/example/demo/StatisticLayout.fxml");
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
    private boolean isUserAcceptToSwitch(){
        if(isTimerInProgress){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning!!!!");
            alert.setHeaderText("This action will reset all progress!");
            alert.setContentText("Are you sure you want to switch?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {
                isTimerInProgress = false;
                return true;
            }
            return false;
        }
        return true;
    }
}
