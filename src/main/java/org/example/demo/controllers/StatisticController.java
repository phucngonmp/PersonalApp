package org.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.example.demo.services.HabitService;
import org.example.demo.services.LearningTimeService;
import org.example.demo.services.TaskService;
import org.example.demo.utils.HibernateUtil;

public class StatisticController {

    private LearningTimeService learningTimeService;
    private TaskService taskService;

    public BarChart taskChart;
    public BarChart timeChart;
    public Label totalLearningTimeLabel;
    public Label totalCompletedTasks;

    @FXML
    public void initialize(){
        learningTimeService = new LearningTimeService(HibernateUtil.getSessionFactory());
        taskService = new TaskService(HibernateUtil.getSessionFactory());
        HabitService habitService = new HabitService(HibernateUtil.getSessionFactory());

        totalLearningTimeLabel.setText((int)(learningTimeService.getTotalLearningTime()) +"m");
        totalCompletedTasks.setText(taskService.getTotalNumberCompletedTasks()+"/"
        + taskService.getTotalNumberTasks());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Learning Time");
        for(Object[] o : habitService.getHabitAndTimeList()){
            series.getData().add(new XYChart.Data<>((String) o[0], (Double) o[1]));
        }
        // Add the series to the bar chart
        timeChart.getData().add(series);

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Task Done");
        for(Object[] o : habitService.getHabitAndTaskList()){
            series2.getData().add(new XYChart.Data<>((String) o[0], (Long) o[1]));
        }
        taskChart.getData().add(series2);
    }

}
