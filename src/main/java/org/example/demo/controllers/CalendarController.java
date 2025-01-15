package org.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;

public class CalendarController {
    protected HandleCalendar handleCalendar;

    public void setDayClickHandler(HandleCalendar handler) {
        this.handleCalendar = handler;
    }

    @FXML
    protected GridPane calendarPane;

    @FXML
    protected Label todayLabel;

    private Label monthLabel;

    private static final int START_ROW = 3;
    private static final int ROW = 8;
    private static final int COLUMN = 7;
    protected final LocalDate today = LocalDate.now();
    private Month thisMonth = today.getMonth();
    private int thisYear = today.getYear();

    @FXML
    protected void initialize() {
        loadCalendar();
        todayLabel.setText(today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    protected void loadCalendar(){

        monthLabel = new Label(thisMonth.name() + ", "+thisYear);
        calendarPane.add(monthLabel, 1, 1);
        GridPane.setColumnSpan(monthLabel, 5);
        GridPane.setHalignment(monthLabel, HPos.CENTER);
        LocalDate firstDay = getDate(1);
        int dayIndex = firstDay.getDayOfWeek().getValue()-1;
        int day = 1;
        int col = dayIndex;
        for(int row = START_ROW; row <= ROW; row++){
            while (col < COLUMN){
                if(day <= thisMonth.length(Year.isLeap(thisYear))){
                    createDayBtn(col, row, day);
                }
                day++;
                col++;
            }
            col = 0;
        }
    }

    protected void createDayBtn(int col, int row, int day){
        Button btn = createBtn(day);
        // load tasks of selected day
        btn.setOnMouseClicked(event ->{
            handleCalendar.onDayClicked(getDate(day));
        });
        calendarPane.add(btn, col, row);
    }

    protected Button createBtn(int day){
        Button btn = new Button();
        btn.setPrefWidth(200);
        btn.setPrefHeight(30);
        btn.setPadding(new Insets(8,8,8,8));
        if(getDate(day).isEqual(today))
            btn.setStyle("-fx-border-color: #ff0000; -fx-border-width: 1px;");
        btn.setText(day +"");
        return btn;
    }

    protected LocalDate getDate(int day){
        return LocalDate.of(thisYear, thisMonth, day);
    }

    @FXML
    protected void today(){
        handleCalendar.onDayClicked(today);
        thisMonth = today.getMonth();
        thisYear = today.getYear();
        clearCalendar();
        loadCalendar();
    }

    @FXML
    protected void nextMonth(){
        if(thisMonth == Month.DECEMBER)
            thisYear++;
        thisMonth = thisMonth.plus(1);
        reloadCalendar();
    }

    protected void clearCalendar() {
        calendarPane.getChildren().remove(monthLabel);
        calendarPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= START_ROW);
    }

    @FXML
    protected void previousMonth(){
        if(thisMonth == Month.JANUARY)
            thisYear--;
        thisMonth = thisMonth.minus(1);
        reloadCalendar();
    }

    protected void reloadCalendar(){
        clearCalendar();
        loadCalendar();
    }


}
