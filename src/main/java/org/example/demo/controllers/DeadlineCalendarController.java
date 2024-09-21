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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeadlineCalendarController extends CalendarController {

    private Set<LocalDate> incompleteDates;
    private Set<LocalDate> completeDates;
    // this store gradient of Colors base on deadline dates, closest deadline date will most red
    // and farthest deadline will be most yellow
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
            // stole from chatGPT
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
        this.incompleteDates = convertToDeadlineDateSet(getDeadlineTasksByStatus(Status.INCOMPLETE));
        this.completeDates = convertToDeadlineDateSet(getDeadlineTasksByStatus(Status.COMPLETE));
        reFillColorsMap();
        super.reloadCalendar();
    }

    private Set<LocalDate> convertToDeadlineDateSet(List<Task> tasks){
        Set<LocalDate> set = new HashSet<>(taskService.convertToTaskDateList(tasks));
        return set;
    }
    private List<Task> getDeadlineTasksByStatus(Status status){
        return taskService.getDeadlineTasksByStatus(status);
    }


    private void reFillColorsMap(){
        int size = incompleteDates.size();
        int i = 0;
        for(LocalDate d : incompleteDates){
            this.colorHashMap.put(d, createColor(i++, size));
        }
    }
    // i stole from chatGPT :)
    private Color createColor(int i, int size) {
        // Normalize index between 0 and 1
        double ratio = (double) i / Math.max(size - 1, 1); // Avoid division by zero

        // Set red to full value (1.0) and increase green from 0 to 1 to get red to yellow transition
        double red = 1.0;  // Red remains constant at 1
        double green = 1.0 - ratio;  // Green increases from 0 to 1

        // Apply lightening if needed
        double lighteningFactor = 0.2; // Adjust this value for more or less lightening
        green = Math.max(0, Math.min(1, green + lighteningFactor));

        return new Color(red, green, 0, 1.0); // Blue remains 0 for the red to yellow transition
    }




}
