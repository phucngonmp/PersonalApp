package org.example.demo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.example.demo.enums.Status;
import org.example.demo.enums.TaskType;
import org.example.demo.models.Task;
import org.example.demo.services.TaskService;
import org.example.demo.ui.TaskRowUI;
import org.example.demo.utils.HibernateUtil;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DeadlineController implements HandleCalendar {

    private CalendarController calendarController;

    @FXML
    private Pane abovePane;

    @FXML
    private VBox deadlineVBox;

    @FXML
    private VBox upcomingVBox;

    @FXML
    private Label taskDeadlineDateLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TextField taskNameTextField;

    @FXML
    private ChoiceBox<TaskType> taskTypeChoiceBox;

    private LocalDate date = LocalDate.now();

    private TaskService taskService;

    @FXML
    public void initialize(){
        taskService = new TaskService(HibernateUtil.getSessionFactory());
        setUpTaskTypeChoiceBox();
        setDateText();
        loadDeadlinesLayout();
        loadUpcomingLayout();
    }

    private void setOnDeleteClick(FontIcon deleteIcon, Task task){
        deleteIcon.setOnMouseClicked(event -> {
            if(task.getStatus() == Status.INCOMPLETE && isConfirmNotice("Do you want to delete this deadline")){
                taskService.deleteTask(task);
                reloadPane();
            }
        });
    }

    private void setOnCheckBoxClick(CheckBox statusCheckBox, Task task){
        statusCheckBox.setOnMouseClicked(event -> {
            // case check the checkbox
            if(statusCheckBox.isSelected()){
                taskService.changeTaskStatus(task, Status.COMPLETE);
                reloadPane();
            }
            // case uncheck the checkbox
            else {
                taskService.changeTaskStatus(task, Status.INCOMPLETE);
                reloadPane();
            }
        });

    }

    private void setUpTaskTypeChoiceBox(){
        ObservableList<TaskType> taskTypeObservableList = createTaskTypeObservableList();
        this.taskTypeChoiceBox.setItems(taskTypeObservableList);
        this.taskTypeChoiceBox.setValue(taskTypeObservableList.get(0));
    }

    private ObservableList<TaskType> createTaskTypeObservableList(){
        List<TaskType> taskTypes = new ArrayList<>();
        taskTypes.add(TaskType.PROJECT_DEADLINE);
        taskTypes.add(TaskType.UNI_DEADLINE);
        ObservableList<TaskType> taskTypeObservableList =
                FXCollections.observableList(taskTypes);
         // we only get DEADLINE types
        return taskTypeObservableList;
    }


    public Pane getAbovePane(){
        return this.abovePane;
    }

    @Override
    public void onDayClicked(LocalDate date) {
        this.date = date;
        setDateText();
        reloadDeadlinesLayout();
    }


    @FXML
    public void addDeadline(){
        Task task = createDeadlineTask();
        if(task != null){
            taskService.addNewTask(task);
            reloadPane();
        }
    }



    private HBox createRowLayout(int index, Task task, boolean isUpcomingRow){
        TaskRowUI taskRowUI = createTaskRowUI(index, task);
        if(isUpcomingRow){
            return createUpcomingRow(taskRowUI);
        }
        return createDeadlineRow(taskRowUI);
    }
    private TaskRowUI createTaskRowUI(int index, Task task){
        TaskRowUI taskRowUI = new TaskRowUI(index, task);
        setOnDeleteClick(taskRowUI.getDeleteIcon(), task);
        setOnCheckBoxClick(taskRowUI.getCheckBox(), task);
        return taskRowUI;
    }
    private void addRowToVBox(VBox layout, HBox rowLayout){
        layout.getChildren().add(rowLayout);
    }

    private HBox createDeadlineRow(TaskRowUI taskRowUI){
        HBox rowLayout = new HBox();
        rowLayout.setSpacing(20);
        rowLayout.setPadding(new Insets(10,10,0,0));
        rowLayout.getChildren().add(taskRowUI.getTaskLabel());
        rowLayout.getChildren().add(taskRowUI.getLayout2());
        return rowLayout;
    }
    private HBox createUpcomingRow(TaskRowUI taskRowUI){
        HBox rowLayout = createDeadlineRow(taskRowUI);
        rowLayout.getChildren().add(taskRowUI.getDaysLeftLabel());
        return rowLayout;
    }


    private Task createDeadlineTask(){
        try {
            validate();
            String taskName = taskNameTextField.getText();
            TaskType taskType = taskTypeChoiceBox.getSelectionModel().getSelectedItem();
            Task task = new Task(taskName, date, Status.INCOMPLETE, 0,null, taskType);
            return task;
        } catch (Exception e) {
            showAlertError(e.getMessage());
            return null;
        }
    }
    private void validate(){
        if(this.taskNameTextField.getText().isEmpty()){
            throw new IllegalArgumentException("You must enter a Deadline name");
        }
        else if(date == null){
            throw new IllegalArgumentException("You must select date for deadline");
        }
        else if(date.isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Deadline Date must be after today");
        }
    }
    private void showAlertError(String error){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);
        alert.show();
    }
    private boolean isConfirmNotice(String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText(message);
        Optional option = alert.showAndWait();
        if(option.isPresent() && option.get() == ButtonType.OK){
            return true;
        }
        return false;
    }

    /* this function will reload all calendar. It will call deadline calendar controller to reload
        incomplete/complete deadlines and reload the calendar UI
     */
    private void reloadPane(){
        calendarController.reloadCalendar();
        reloadDeadlinesLayout();
        reloadUpcomingLayout();
    }
    private void reloadUpcomingLayout(){
        clearVBoxLayout(upcomingVBox);
        loadUpcomingLayout();
    }
    private void reloadDeadlinesLayout(){
        clearVBoxLayout(deadlineVBox);
        loadDeadlinesLayout();
    }
    private void loadDeadlinesLayout(){
        int index = 1;
        for(Task task : taskService.getDeadlineTasksByDate(date)){
            addRowToVBox(deadlineVBox, createRowLayout(index++, task, false));
        }
    }
    private void loadUpcomingLayout(){
        int index = 1;
        for(Task task : taskService.getUpcomingDeadlineTasks()){
            addRowToVBox(upcomingVBox, createRowLayout(index++, task, true));
        }
    }
    private void clearVBoxLayout(VBox vBox){
        vBox.getChildren().removeIf(node ->
                vBox.getChildren().indexOf(node) > 0
        );

    }

    public void setCalendarController(CalendarController calendarController){
        this.calendarController = calendarController;
    }

    private void setDateText(){
        this.dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        this.taskDeadlineDateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }




}
