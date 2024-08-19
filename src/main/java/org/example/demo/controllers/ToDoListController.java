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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.example.demo.enums.Status;
import org.example.demo.models.Habit;
import org.example.demo.models.Task;
import org.example.demo.services.HabitService;
import org.example.demo.services.TaskService;
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
    private LocalDate date;

    @FXML
    private TextField nameTaskTextField;

    @FXML
    private ChoiceBox<Habit> habitChoiceBox;

    @FXML
    private ChoiceBox<String> timeChoiceBox;

    @FXML
    private Button addBtn;

    @FXML
    private Button nextDayButton;

    @FXML
    private Button previouDayButton;

    @FXML
    private Text title;

    @FXML
    private FontIcon icon;

    private TaskService taskService;
    private HabitService habitService;

    private static final int TASKS_START_ROW = 5;

    @FXML
    private void initialize() {

        taskService = new TaskService(HibernateUtil.getSessionFactory());
        habitService = new HabitService(HibernateUtil.getSessionFactory());

        List<Habit> habitList = habitService.getAllHabitTasks();

        // Set initial date is today
        date = LocalDate.now();
        showDate(date);
        dateText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 22));
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

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
            String taskName = nameTaskTextField.getText();
            Habit habit = habitChoiceBox.getSelectionModel().getSelectedItem();
            String taskTimeString = timeChoiceBox.getSelectionModel().getSelectedItem();
            double taskTime = 0;
            if(!taskTimeString.equals("None")){
                taskTimeString = taskTimeString.substring(0, taskTimeString.length()-1);
                taskTime = Double.parseDouble(taskTimeString);
            }
            int status = Status.INCOMPLETE.getCode();

            Task task = new Task(taskName, date, status, taskTime, habit);
            taskService.addNewTask(task);
            nameTaskTextField.clear();
            timeChoiceBox.setValue("None");
            loadTasks();
        }
    }
    // this function also clear all task first
    private void loadTasks(){
        List<Task> taskList = taskService.getTasksByDate(date);

        // clear tasks
        leftPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) >= TASKS_START_ROW);
        int rowIndex = TASKS_START_ROW; // the start row index of tasks
        int taskIndex = 1;

        for(Task task : taskList){
            setUpTask(task, taskIndex, rowIndex, taskList);
            rowIndex++;
            taskIndex++;
        }
    }
    private void setUpTask(Task task, int taskIndex, int rowIndex, List<Task> tasks ){

        // will be added to column 0 of leftpane
        Label taskNameLabel = new Label(taskIndex + ". " + task.getName());

        // will be added to column 1 of leftpane
        FontIcon habitIcon = new FontIcon(task.getHabit().getIcon());

        FontIcon timeIcon = new FontIcon();
        Label timeLabel = new Label();
        if(task.getTaskTime() > 0){
            timeIcon.setIconCode(FontAwesomeSolid.CLOCK);
            timeIcon.setIconSize(20);
            timeLabel.setText(task.getTaskTime() + "h");
        }

        Label streakLabel = new Label(Integer.toString(task.getHabit().getStreak()));
        //will be added to column 2 of leftpane
        CheckBox taskCheckbox = new CheckBox();
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);

        habitIcon.setIconSize(20);
        deleteIcon.setIconSize(20);
        handleDeleteIcon(deleteIcon, task);
        handleTaskCheckBox(taskCheckbox, task, tasks);

        // create the layout for column 1 include habit icon and streak number
        HBox column1Layout = createColumnLayout(List.of(timeIcon, timeLabel, habitIcon, streakLabel));

        // create a checkbox and delete icon layout
        HBox column2Layout = createColumnLayout(List.of(taskCheckbox, deleteIcon));

        // add all to left pane layout columns and set their positions
        leftPane.add(taskNameLabel, 0, rowIndex);
        leftPane.add(column1Layout, 1, rowIndex);
        leftPane.add(column2Layout, 2, rowIndex);
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
            if((date.isEqual(LocalDate.now()) || date.isAfter(LocalDate.now())) && task.getStatus() == Status.INCOMPLETE.getCode()){
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

    // this function will change habit's streak whenever click checkbox
    private void handleTaskCheckBox(CheckBox checkBox, Task task, List<Task> tasks){

        if(task.getStatus() == Status.COMPLETE.getCode()){
            checkBox.setSelected(true);
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate lastDate = currentDate.minusDays(1);
        LocalDate latestStreakDate = task.getHabit().getLatestDate();

        Habit habit = task.getHabit();
        // count tasks of a habit;
        int tasksOfAHabit = habitService.countHabit(tasks, habit);

        // only current day and one day before it are able to change status
        // note: date variable is determined by which date user is interacting with
        if(date.isAfter(currentDate) || date.isBefore(lastDate)){
            checkBox.setDisable(true);
        }

        // handle mouse clicked
        checkBox.setOnMouseClicked(mouseEvent -> {
            // task has task's time will only complete by using timer
            if(task.getTaskTime() > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You can only complete this task by using timer");
                alert.show();
                checkBox.setSelected(task.getStatus() == Status.COMPLETE.getCode() ? true : false);
                mouseEvent.consume();
            }
            else{
                // case: check the checkbox
                if(checkBox.isSelected()){
                    // we only increase streak if the latest streak date is before current date or just have one task of a habit in current date

                    // because if latest modified streak date is current date and have 2 more tasks of a same habit this mean another task already increase streak,
                    //      so we just change status of task as done but not increase streak in this case;
                    taskService.changeTaskStatus(task, Status.COMPLETE);
                    if(latestStreakDate.isBefore(currentDate) || tasksOfAHabit == 1 && latestStreakDate.isEqual(currentDate)){
                        habitService.changeStreak(habit.getId(), currentDate, 1);
                    }

                }
                // case: uncheck the checkbox
                else{
                    // in this case we will decrease the streak and also make the last modified day of streak
                    // become one day before the last modified day, only if the date which we're interacting has one task of this habit
                    // or all completed task of this habit is one
                    int completedTasksOfAHabit = (int)tasks.stream()
                            .filter(task1 -> task1.getHabit().equals(habit))
                            .filter(task1 -> task1.getStatus() == Status.COMPLETE.getCode())
                            .count();
                    if(tasksOfAHabit == 1 || completedTasksOfAHabit == 1){
                        habitService.changeStreak(habit.getId(), habit.getLatestDate().minusDays(1), -1);
                    }
                    taskService.changeTaskStatus(task, Status.INCOMPLETE);

                }

                // update the UI
                loadTasks();
            }

        });
    }

    private void setUpHabitChoiceBox(ChoiceBox<Habit> habitChoiceBox, ObservableList<Habit> habitList){
        habitChoiceBox.setItems(habitList);
        habitChoiceBox.setValue(habitList.get(0));
        habitChoiceBox.setConverter(new StringConverter<Habit>() {
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
        showDate(date);
        loadTasks();
    }

    @FXML
    private void handlePreviousDayButton(){
        date = date.minusDays(1);
        showDate(date);
        loadTasks();
    }
}
