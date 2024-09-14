package org.example.demo.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.example.demo.enums.Status;
import org.example.demo.models.Habit;
import org.example.demo.models.Task;
import org.example.demo.services.HabitService;
import org.example.demo.services.LearningTimeService;
import org.example.demo.services.TaskService;
import org.example.demo.utils.HibernateUtil;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TimerController {
    @FXML
    private Pane mainPane;

    @FXML
    private Label timerLabel;

    @FXML
    private Button startBtn;

    @FXML
    private Label chosenTaskLabel;

    @FXML
    private ChoiceBox<Habit> habitChoiceBox = new ChoiceBox<>();

    @FXML
    private TextField inputTimeTxtField;


    @FXML
    private TableView<Task> taskTableView;
    @FXML private TableColumn<Task, String> taskNameColumn;
    @FXML private TableColumn<Task, Integer> taskTimeColumn;
    @FXML private TableColumn<Task, Status> taskStatusColumn;
    @FXML private TableColumn<Task, LocalDate> taskDateColumn;

    private TaskService taskService;
    private HabitService habitService;
    private LearningTimeService learningTimeService;
    private ObservableList<Task> tasks;


// variables for pomodoro
    private int rep;
    private int pomodoroTime;
    private int breakTime;
    private Task pomodoroSelectedTask;
    private boolean isPomodoroModeON = false;
    private boolean isBreak = false;

    @FXML private Label poLabel;
    @FXML private Label poTaskLabel;
    @FXML private HBox poHBox;
    @FXML private Label poInfoLabel;
    @FXML private FontIcon poIcon;
    private int poHBoxElementsCount;

// variables for timer
    private Duration duration;
    // initial value is 30m
    private Integer time = 30;
    private Timeline timeline;
    // is timeline running
    private boolean isRunning = false;
    // is the timeline already set
    private boolean isSet = false;
    private Task chosenTask;

    private ObservableList<Task> getTodayTasks(){
        List<Task> tempTasks = taskService.getAllTasks().stream()
                .filter(task -> task.getDate().isEqual(LocalDate.now()))
                .filter(task -> task.getTaskTime() > 0)
                .sorted(Comparator.comparing(Task::getStatus).reversed())
                .collect(Collectors.toList());
        return FXCollections.observableList(tempTasks);
    }
    private void resetTableViewTasks(){
        tasks = getTodayTasks();
        ObservableList<Task> taskObservableList = FXCollections.observableList(tasks);
        taskTableView.setItems(taskObservableList);
    }


    @FXML
    private void initialize() {
        taskService = new TaskService(HibernateUtil.getSessionFactory());
        habitService = new HabitService(HibernateUtil.getSessionFactory());
        learningTimeService = new LearningTimeService(HibernateUtil.getSessionFactory());


        tasks = getTodayTasks();
        timerLabel.setText("30:00");
        startBtn.setGraphic(new FontIcon(FontAwesomeSolid.PLAY));
        habitService.fillHabitChoiceBox(habitChoiceBox);
        formatTxtField(inputTimeTxtField);
        setUpTaskTableView();
        handleMouseClickedTaskTable();

    }

    private void setUpTaskTableView() {
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        taskTimeColumn.setCellValueFactory(new PropertyValueFactory<>("taskTime"));
        taskTimeColumn.setStyle("-fx-alignment: CENTER");
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskStatusColumn.setCellFactory(column -> new TableCell<Task, Status>() {
            private final FontIcon completeIcon = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
            private final FontIcon incompleteIcon = new FontIcon(FontAwesomeSolid.TIMES_CIRCLE);
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Convert status value to corresponding text
                    setGraphic(status == Status.INCOMPLETE ? incompleteIcon : completeIcon);
                }
                setAlignment(Pos.CENTER);
                completeIcon.setIconColor(Color.GREEN);
                incompleteIcon.setIconColor(Color.RED);
            }
        });

        ObservableList<Task> taskObservableList = FXCollections.observableList(tasks);
        taskTableView.setItems(taskObservableList);
    }


    @FXML
    public void handlePomodoroModeBtn(){
        if(isRunning){
            return;
        }
        // create a new dialog for Pomodoro mode
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Pomodoro Mode");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField pomodoroTimeTxtField = new TextField("40");
        TextField breakTimeTxtField = new TextField("5");
        ChoiceBox<Integer> repChoiceBox = new ChoiceBox<>(FXCollections.observableList(List.of(1,2,3,4,5)));
        // set up for rep choice box
        repChoiceBox.setValue(3);
        onChangeRep(repChoiceBox, pomodoroTimeTxtField);

        // set up for task choice box
        List<Task> incompleteTask = tasks.filtered(task -> task.getStatus() == Status.INCOMPLETE);
        ObservableList<Task> choiceBoxItems = FXCollections.observableArrayList(incompleteTask);
        choiceBoxItems.add(null);
        ChoiceBox<Task> choiceBox = new ChoiceBox<>(choiceBoxItems);
        choiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Task task) {
                return task == null ? "None" : task.getName();
            }

            @Override
            public Task fromString(String s) {
                return null;
            }
        });
        onChangeTask(choiceBox, pomodoroTimeTxtField, repChoiceBox);


        pomodoroTimeTxtField.setPrefWidth(100);
        breakTimeTxtField.setPrefWidth(100);
        repChoiceBox.setPrefWidth(100);
        formatTxtField(pomodoroTimeTxtField);
        formatTxtField(breakTimeTxtField);

        gridPane.add(new Label("Pomodoro"), 0,0);
        gridPane.add(new Label("Break"), 1, 0);
        gridPane.add(new Label("Rep"), 0, 2);
        gridPane.add(new Label("Task"), 1, 2);
        gridPane.add(pomodoroTimeTxtField, 0, 1);
        gridPane.add(breakTimeTxtField, 1, 1);
        gridPane.add(repChoiceBox, 0, 3);
        gridPane.add(choiceBox, 1, 3);

        ButtonType startButtonType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(startButtonType);

        // Set the dialog content
        dialog.getDialogPane().setContent(gridPane);
        // Handle the result of the dialog
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == startButtonType) {
                // Retrieve input values here
                pomodoroTime = Integer.parseInt(pomodoroTimeTxtField.getText());
                breakTime = Integer.parseInt(breakTimeTxtField.getText());
                rep = repChoiceBox.getValue();
                pomodoroSelectedTask = choiceBox.getValue();
                turnOnPomodoroMode();
            }
            return null;
        });
        dialog.showAndWait();
    }
    @FXML
    private void turnOffPomodoroMode(){
        mainPane.setStyle("-fx-background-color: #F4F4F4;");
        if(isRunning){
            return;
        }
        timerLabel.setText("00:00");
        mainPane.getChildren().remove(poIcon);
        poInfoLabel.setText("");
        isPomodoroModeON = false;
        habitChoiceBox.setValue(null);
        poLabel.setText("");
        habitChoiceBox.setDisable(false);
        poHBox.getChildren().clear();
        poTaskLabel.setText("");
    }
    private void turnOnPomodoroMode(){
        mainPane.setStyle("-fx-background-color: #FFCCCC;");
        time = pomodoroTime*rep;
        poHBoxElementsCount = rep*2-1;
        poHBox.getChildren().clear();
        if(!mainPane.getChildren().contains(poIcon))
            mainPane.getChildren().add(poIcon);
        isSet = false;
        isPomodoroModeON = true;
        poLabel.setText("Pomodoro Mode: ON       Time: " + time +"m");
        poInfoLabel.setText("Pomodoro: " + pomodoroTime + "m, Break: " + breakTime+"m");
        switchPeriod();
        if(pomodoroSelectedTask == null){
            poTaskLabel.setText("");
            habitChoiceBox.setDisable(false);
        }else{
            poTaskLabel.setText("Task: " +pomodoroSelectedTask.getName());
            habitChoiceBox.setDisable(true);
        }
        setUpRepIcons(rep);
        chosenTaskLabel.setText("");
        timerLabel.setText(pomodoroTime+":00");
    }
    private void switchPeriod(){
        if(isBreak){
            poIcon.setIconCode(FontAwesomeSolid.SEEDLING);
            poIcon.setIconColor(Color.GREEN);
            mainPane.setStyle("-fx-background-color: #CCFFCC;");
        } else {
            poIcon.setIconCode(FontAwesomeSolid.PEPPER_HOT);
            poIcon.setIconColor(Color.RED);
            mainPane.setStyle("-fx-background-color: #ffa494;");

        }
        poIcon.setIconSize(100);
    }
    private void setUpRepIcons(int rep){
        addIcon(FontAwesomeSolid.PEPPER_HOT, Color.RED);
        rep--;
        while (rep > 0){
            addIcon(FontAwesomeSolid.SEEDLING, Color.GREEN);
            addIcon(FontAwesomeSolid.PEPPER_HOT, Color.RED);
            rep--;
        }
    }
    private void addIcon(FontAwesomeSolid iconName, Color color){
        FontIcon icon = new FontIcon(iconName);
        icon.setIconSize(25);
        icon.setIconColor(color);
        poHBox.getChildren().add(icon);
    }
    private void onChangeRep(ChoiceBox<Integer> choiceBox, TextField pomodoro){
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(pomodoroSelectedTask != null) {
                double taskTime = pomodoroSelectedTask.getTaskTime();  // Assuming task time is in minutes
                int pomodoroTime = (int) Math.ceil( (taskTime * 60) / newValue);
                pomodoro.setText(pomodoroTime + "");
            }
        });

    }
    private void onChangeTask(ChoiceBox<Task> choiceBox, TextField pomodoro, ChoiceBox<Integer> rep){
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null && rep.getValue() != null) {
                pomodoroSelectedTask = newValue;
                double taskTime = newValue.getTaskTime();  // Assuming task time is in minutes
                int repetition = rep.getValue();
                int pomodoroTime = (int)(taskTime * 60) / repetition;  // Convert task time to seconds and divide by repetitions
                pomodoro.setText(pomodoroTime+"");
            }
            else{
                pomodoroSelectedTask = null;
            }
        });
    }

    private void handleMouseClickedTaskTable(){
        taskTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                // Optionally, set a different style if the row is not empty
                if (!empty) {
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 1) {
                            if (!isRunning && !isPomodoroModeON) {
                                // Handle row click
                                // if choose same task or a complete task will reset
                                if (chosenTask == task || task.getStatus() == Status.COMPLETE) {
                                    reset();
                                } else {
                                    isSet = false;
                                    chosenTask = task;
                                    time = (int) (task.getTaskTime() * 60);
                                    habitChoiceBox.setValue(task.getHabit());
                                    habitChoiceBox.setDisable(true);
                                    chosenTaskLabel.setText("task: " + task.getName());
                                    String formattedTaskTime = String.format("%.0f", task.getTaskTime() * 60);
                                    timerLabel.setText(formattedTaskTime + ":00");
                                }

                            }
                        }
                    });
                }
            }
        });

    }


    @FXML
    public void handleSetTimeBtn(){
        if(inputTimeTxtField.getText().isEmpty() || isRunning || isPomodoroModeON){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("please enter minutes or stop timeline or exit pomodoro mode");
            alert.showAndWait();
        }
        else{
            reset();
            isSet = false;
            time = Integer.parseInt(inputTimeTxtField.getText());
            timerLabel.setText(inputTimeTxtField.getText()+":00");
            inputTimeTxtField.setText("");
        }
    }
    private Duration getTime() {
        // get and convert to Duration form timer label
        String timeString = timerLabel.getText();
        String [] parts = timeString.split(":");

        long m = Long.parseLong(parts[0]);
        long s = Long.parseLong(parts[1]);

        return Duration.ofHours(0).plusMinutes(m).minusSeconds(s);
    }

    private void formatTxtField(TextField textField){
        TextFormatter formatter = new TextFormatter<>(change -> {
            // limit length to 3
            if(change.getControlNewText().length() > 3){
                return null;
            }
            // only accept digit and blank(backspace,...)
            if(change.getText().matches("\\d*")){
                return change;
            }
            return null;
        });

        textField.setTextFormatter(formatter);
    }

    @FXML
    public void startTimer(){
        if(getTime().isZero())
            return;
        // case: start timer so create new timeline and run
        if(!isSet){
            duration = getTime();
            if(isPomodoroModeON && !isBreak){
                rep--;
                System.out.println(rep);
            }
            KeyFrame keyFrame = new KeyFrame(javafx.util.Duration.seconds(1), actionEvent -> {
                duration = duration.minusSeconds(1);
                long m,s;
                String formatter;
                if(duration.toMinutes() == 0){
                    m = 0;
                    s = duration.getSeconds();
                }else{
                    m = duration.toMinutes();
                    s = duration.minusMinutes(m).getSeconds();
                }
                if(m > 99){
                    formatter = String.format("%03d:%02d", m, s);
                }else {
                    formatter = String.format("%02d:%02d", m, s);
                }
                timerLabel.setText(formatter);
                if(duration.isZero() || duration.isNegative()){
                    finishTimeLine();
                }
            });
            timeline = new Timeline(keyFrame);
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            // adjust flags
            isRunning = true;
            isSet = true;
            startBtn.setGraphic(new FontIcon(FontAwesomeSolid.STOP));
            habitChoiceBox.setDisable(true);
        }
        // case: timeline already set or isSet = true
        else{
            // timeline is running click this button will stop timeline
            if(isRunning){
                stopTimeLine();
            }
            // timeline is stop click will continue timeline
            else {
                timeline.play();
                isRunning = true;
                startBtn.setGraphic(new FontIcon(FontAwesomeSolid.STOP));
            }
        }

    }
    private void finishTimeLine(){
        stopTimeLine();
        isSet = false;
        // if in pomodoro mode
        if(isPomodoroModeON){
            Node node = poHBox.getChildren().get(--poHBoxElementsCount);
            if(rep > 0){
                if(node instanceof FontIcon icon){
                    icon.setIconColor(Color.GRAY);
                }
                if(isBreak){
                    isBreak = false;
                    timerLabel.setText(pomodoroTime+":00");
                }else{
                    isBreak = true;
                    timerLabel.setText(breakTime+":00");
                }
                switchPeriod();
            }
            else {
                if(node instanceof FontIcon icon){
                    icon.setIconColor(Color.GRAY);
                }
                finishTimer();
                reset();
            }
        }
        else {
            finishTimer();
            reset();
        }


    }

    public void stopTimeLine(){
        isRunning = false;
        startBtn.setGraphic(new FontIcon(FontAwesomeSolid.PLAY));
        timeline.stop();
    }

    private void finishTimer(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulation");
        if(chosenTask != null){
            taskService.changeTaskStatus(chosenTask, Status.COMPLETE);
            alert.setContentText("You have done " + chosenTask.getName() +" task");
        }
        if(pomodoroSelectedTask != null && rep == 0){
            taskService.changeTaskStatus(pomodoroSelectedTask, Status.COMPLETE);
        }
        Habit habit = habitChoiceBox.getSelectionModel().getSelectedItem();
        learningTimeService.saveLearningTime(time, habit);
        resetTableViewTasks();

        alert.setContentText("Time over you learned: "+ time +"m");
        alert.show();
    }

    private void reset(){
        chosenTask = null;
        //habitChoiceBox.setValue(null);
        habitChoiceBox.setDisable(false);
        chosenTaskLabel.setText("No task selected");
        timerLabel.setText("00:00");
    }







}
