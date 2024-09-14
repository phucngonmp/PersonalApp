package org.example.demo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.example.demo.enums.Status;
import org.example.demo.models.Habit;
import org.example.demo.models.Task;
import org.example.demo.services.HabitService;
import org.example.demo.services.TaskService;
import org.example.demo.ui.TaskRowUI;
import org.example.demo.utils.HibernateUtil;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ToDoListController {
    @FXML
    private GridPane leftPane;

    @FXML
    private VBox rightPane;

    @FXML
    private Text dateText;
    public LocalDate date;

    @FXML
    private TextField nameTaskTextField;

    @FXML
    private ChoiceBox<Habit> habitChoiceBox;

    @FXML
    private ChoiceBox<String> timeChoiceBox;
    
    private TaskService taskService;
    private HabitService habitService;
    private List<Task> tasks;

    private static final int TASKS_START_ROW = 5;

    public VBox getRightPane(){
        return this.rightPane;
    }

    @FXML
    private void initialize() {
        taskService = new TaskService(HibernateUtil.getSessionFactory());
        habitService = new HabitService(HibernateUtil.getSessionFactory());

        // Set initial date is today
        date = LocalDate.now();
        showDate(date);

        List<Habit> habitList = habitService.getAllHabitTasks();


        loadTasks();

        /*code for right pane begin here */

        // convert habit list to observable list
        ObservableList<Habit> observableHabitList = FXCollections.observableList(habitList);
        setUpHabitChoiceBox(habitChoiceBox, observableHabitList);

    }

    @FXML
    private void addNewTask(){
        if(date.isBefore(LocalDate.now())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You can't add a task in previous days");
            alert.show();
        }
        else if(nameTaskTextField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter task's name");
            alert.show();
        }
        else{
            Task task = getTask();
            taskService.addNewTask(task);
            nameTaskTextField.clear();
            timeChoiceBox.setValue("None");
            loadTasks();
        }
    }

    private Task getTask() {
        String taskName = nameTaskTextField.getText();
        Habit habit = habitChoiceBox.getSelectionModel().getSelectedItem();
        String taskTimeString = timeChoiceBox.getSelectionModel().getSelectedItem();
        double taskTime = 0;
        if(!taskTimeString.equals("None")){
            taskTimeString = taskTimeString.substring(0, taskTimeString.length()-1);
            taskTime = Double.parseDouble(taskTimeString);
        }
        return new Task(taskName, date, Status.INCOMPLETE, taskTime, habit);
    }

    // this function also clear all task first
    public void loadTasks(){
        showDate(date);
        tasks = taskService.getTasksByDate(date);

        // clear tasks
        leftPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= TASKS_START_ROW);
        int rowIndex = TASKS_START_ROW; // the start row index of tasks
        int taskIndex = 1;

        for(Task task : tasks){
            // setUpTask(task, taskIndex, rowIndex, taskList);
            includeTaskRowUI(createTaskRowUI(task, taskIndex), rowIndex);
            rowIndex++;
            taskIndex++;
        }
    }
    private TaskRowUI createTaskRowUI(Task task, int taskIndex){
        TaskRowUI taskRowUI = new TaskRowUI(taskIndex, task);
        handleTaskCheckBox(taskRowUI.getCheckBox(), task);
        handleDeleteIcon(taskRowUI.getDeleteIcon(), task);
        return taskRowUI;
    }

    private void includeTaskRowUI(TaskRowUI taskRowUI, int rowIndex){
        Label taskNameLabel = taskRowUI.getTaskLabel();
        HBox column1Layout = taskRowUI.getLayout1();
        HBox column2Layout = taskRowUI.getLayout2();
        leftPane.add(taskNameLabel, 0, rowIndex);
        leftPane.add(column1Layout, 1, rowIndex);
        leftPane.add(column2Layout, 2, rowIndex);

        // decor
        GridPane.setHalignment(taskNameLabel, HPos.LEFT);
        GridPane.setHalignment(column1Layout, HPos.RIGHT);
        GridPane.setHalignment(column2Layout, HPos.RIGHT);
    }

    private HBox createColumnLayout(List<Node> nodes){
        HBox layout = new HBox();
        layout.setSpacing(20);
        layout.setAlignment(Pos.TOP_RIGHT);
        nodes.forEach(node -> layout.getChildren().add(node));
        return layout;
    }
    private void handleDeleteIcon(FontIcon deleteIcon, Task task){
        // ask user to confirm delete action
        deleteIcon.setOnMouseClicked(mouseEvent -> {
            if((date.isEqual(LocalDate.now()) || date.isAfter(LocalDate.now())) && task.getStatus() == Status.INCOMPLETE){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this task?");
                alert.setContentText(task.getName());

                Optional<ButtonType> option = alert.showAndWait();
                if(option.isPresent() && option.get() == ButtonType.OK){
                    taskService.deleteTask(task);
                    loadTasks();
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Note");
                alert.setHeaderText("You can't delete this task");
                alert.setContentText("you can only delete incomplete task and not be in the past");
                alert.show();
            }

        });
    }

    // this function will change habit's active days whenever click checkbox
    private void handleTaskCheckBox(CheckBox checkBox, Task task){
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDate = currentDate.minusDays(1);
        // only current day and one day before it are able to change status
        // note: date variable is determined by which date user is interacting with
        if(date.isAfter(currentDate) || date.isBefore(lastDate)){
            checkBox.setDisable(true);
        }

        Habit selectedHabit = task.getHabit();
        int completedTasks = taskService.getCompletedTasksOfHabit(tasks, selectedHabit);

        // handle mouse clicked
        checkBox.setOnMouseClicked(mouseEvent -> {
            // task has task's time will only complete by using timer
            if(task.getTaskTime() > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You can only complete this task by using timer");
                alert.show();
                checkBox.setSelected(task.getStatus() == Status.COMPLETE);
                mouseEvent.consume();
            }
            else{
                // case: check the checkbox
                if(checkBox.isSelected()){
                    taskService.changeTaskStatus(task, Status.COMPLETE);
                    if(completedTasks == 0){
                        habitService.updateActiveDays(selectedHabit.getId(), 1);
                    }
                }
                // case: uncheck the checkbox
                else{
                    taskService.changeTaskStatus(task, Status.INCOMPLETE);
                    if(completedTasks == 1){
                        habitService.updateActiveDays(selectedHabit.getId(), -1);
                    }
                }
                // update the UI
                loadTasks();
            }

        });
    }

    private void setUpHabitChoiceBox(ChoiceBox<Habit> habitChoiceBox, ObservableList<Habit> habitList){
        habitChoiceBox.setItems(habitList);
        habitChoiceBox.setValue(habitList.get(0));
        habitChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Habit habit) {
                return habit.getName();
            }

            @Override
            public Habit fromString(String s) {
                return null;
            }
        });
    }
    private void showDate(LocalDate date){
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        dateText.setText(formattedDate);
    }
    @FXML
    private void handleNextDayButton(){
        date = date.plusDays(1);
        loadTasks();
    }

    @FXML
    private void handlePreviousDayButton(){
        date = date.minusDays(1);
        loadTasks();
    }
}
