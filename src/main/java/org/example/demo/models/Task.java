package org.example.demo.models;

import jakarta.persistence.*;
import org.example.demo.enums.Status;
import org.example.demo.enums.TaskType;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {

    @Column(name = "task_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    private String name;
    private LocalDate date;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(name = "task_time")
    private double taskTime;

    @Column(name = "type")
    private TaskType type;


    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;

    public Task() {

    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public double getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(double taskTime) {
        this.taskTime = taskTime;
    }

    public Task(String name, LocalDate date, Status status, double taskTime, Habit habit, TaskType type) {
        this.name = name;
        this.date = date;
        this.status = status;
        this.taskTime = taskTime;
        this.habit = habit;
        this.type = type;
    }
}
