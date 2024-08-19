package org.example.demo.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "habit_tasks")
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "streak")
    private int streak;

    @Column(name = "icon")
    private String icon;

    @Column(name = "latest")
    private LocalDate latestDate;

    @OneToMany(mappedBy = "habit")
    private List<Task> taskList;

    @OneToMany(mappedBy = "habit")
    private List<LearningTime> learningTimeList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public LocalDate getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(LocalDate latestDate) {
        this.latestDate = latestDate;
    }

    public List<Task> getTaskList() {
        return taskList;
    }
    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public String toString() {
        return "[id: " + this.id + ", name: " + this.name + ", streak: " + this.streak + ", icon: " +this.icon+"]";
    }
}
