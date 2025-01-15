package org.example.demo.controllers;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.example.demo.enums.Status;
import org.example.demo.models.Task;
import org.example.demo.services.TaskService;
import org.example.demo.utils.HibernateUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DeadlineCalendarController extends CalendarController {

    private Set<LocalDate> incompleteDates;
    private Set<LocalDate> completeDates;
    // this store gradient of Colors based on deadline dates, closest deadline date will most red
    // and farthest deadline will be most lightyellow
    private HashMap<LocalDate, Color> colorHashMap = new HashMap<>();
    private TaskService taskService;

    @Override
    protected void createDayBtn(int col, int row, int day) {
        Button btn = createBtn(day);
        LocalDate thisDate = getDate(day);
        // if a date contains both incomplete and complete deadlines it will be colored red - yellow
        // only date contains no incomplete deadlines has green color
        if(incompleteDates.contains(thisDate)){
            Color color = colorHashMap.get(thisDate);
            btn.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        } else if(completeDates.contains(thisDate)){
            btn.setStyle("-fx-background-color: lightgreen;");

        }
        btn.setOnMouseClicked(event ->{
            handleCalendar.onDayClicked(thisDate);
        });
        calendarPane.add(btn, col, row);
    }

    @Override
    protected void initialize() {
        taskService = new TaskService(HibernateUtil.getSessionFactory());
        reloadCalendar();
        todayLabel.setText(today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    @Override
    protected void reloadCalendar() {
        List<Task> sortedIncompleteDeadlines =
                getDeadlineTasksByStatus(Status.INCOMPLETE).stream()
                        .sorted(Comparator.comparing(Task::getDate))
                        .collect(Collectors.toList());
        this.incompleteDates = convertToDeadlineDateSet(sortedIncompleteDeadlines);
        this.completeDates = convertToDeadlineDateSet(getDeadlineTasksByStatus(Status.COMPLETE));
        reFillColorsMap();
        super.reloadCalendar();
    }

    private Set<LocalDate> convertToDeadlineDateSet(List<Task> tasks){
        Set<LocalDate> set = new LinkedHashSet<>((taskService.convertToTaskDateList(tasks)));
        return set;
    }
    private List<Task> getDeadlineTasksByStatus(Status status){
        return taskService.getDeadlineTasksByStatus(status);
    }


    private void reFillColorsMap(){
        int size = incompleteDates.size();
        int i = 0;
        for(LocalDate d : incompleteDates){
            System.out.println(d.toString());
            this.colorHashMap.put(d, createColor(i++, size));
        }
    }
    // i stole from chatGPT :)
    private Color createColor(int i, int size) {
        // Normalize index between 0 and 1
        double ratio = (double) i / Math.max(size - 1, 1); // Avoid division by zero

        // Set red to remain full (1.0) and green to increase from 0 to 1 as i increases
        double red = 1.0;      // Red remains constant at 1
        double green = ratio;  // Green increases from 0 to 1

        // Ensure light yellow when i is low
        if (i < size * 0.05) {
            green = Math.min(1.0, green + 0.2); // Apply slight increase for light yellow effect
        }

        return new Color(red, green, 0, 1.0); // Blue remains 0 for red to yellow transition
    }






}
