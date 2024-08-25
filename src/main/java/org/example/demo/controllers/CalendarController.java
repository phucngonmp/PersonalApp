package org.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.Month;

public class CalendarController {
    @FXML
    private ToDoListController toDoListController;

    // This method is called to set the ToDoListController instance
    public void setToDoListController(ToDoListController controller) {
        this.toDoListController = controller;
    }

    @FXML
    private GridPane calendarPane;

    private static final int ROW = 5;
    private static final int COLUMN = 7;
    private LocalDate today = LocalDate.now();
    private Month thisMonth = today.getMonth();

    @FXML
    private void initialize() {
        LocalDate firstDay = getDate(1);
        int dayIndex = firstDay.getDayOfWeek().getValue()-1;
        int day = 1;
        int col = dayIndex;
        for(int row = 1; row <= ROW; row++){
            while (col < COLUMN){
                if(day <= 31){
                    createDayBtn(col, row, day);
                }
                day++;
                col++;
            }
            col = 0;
        }
    }

    private void createDayBtn(int col, int row, int day){
        Button btn = new Button();
        btn.setPrefWidth(100);
        btn.setPadding(new Insets(8,8,8,8));
        if(day == today.getDayOfMonth())
            btn.setStyle("-fx-border-color: #ff0000; -fx-border-width: 1px;");
        btn.setText(day +"");
        btn.setOnMouseClicked(event ->{
            toDoListController.date = getDate(day);
            toDoListController.loadTasks();
        });
        calendarPane.add(btn, col, row);
    }

    public LocalDate getDate(int day){
        return LocalDate.of(today.getYear(), thisMonth, day);
    }

}
