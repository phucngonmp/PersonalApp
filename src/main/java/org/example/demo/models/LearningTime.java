package org.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "learning_time")
public class LearningTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate date;
    private double time;

    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }
    public LearningTime(){

    }

    public LearningTime(int id, LocalDate date, double time, Habit habit) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.habit = habit;
    }
}
