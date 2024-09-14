package org.example.demo.services;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;
import org.example.demo.models.Habit;
import org.example.demo.models.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitService {

    private final SessionFactory sessionFactory;

    public HabitService(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public int countHabit(List<Task> tasks, Habit habit){
        return (int) tasks.stream()
                    .filter(task -> task.getHabit().equals(habit))
                    .count();
    }

    public void fillHabitChoiceBox(ChoiceBox<Habit> habitChoiceBox){
        List<Habit> habits = new ArrayList<>();
        habits.add(null);
        habits.addAll(getAllHabitTasks());
        habitChoiceBox.setItems(FXCollections.observableList(habits));
        habitChoiceBox.setValue(habits.get(0));
        habitChoiceBox.setConverter(new StringConverter<Habit>() {
            @Override
            public String toString(Habit habit) {
                return habit == null ? "None" : habit.getName();
            }

            @Override
            public Habit fromString(String s) {
                return null;
            }
        });
    }


    public List<Habit> getAllHabitTasks(){
        List<Habit> habitList = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            String hql = "FROM Habit ";
            habitList =  session.createQuery(hql, Habit.class).getResultList();
            transaction.commit();
        }
        catch (Exception e){
            if (transaction != null) {
                transaction.rollback(); // Rollback the transaction in case of failure
            }
        }
        finally {
            session.close();
        }
        return habitList;
    }
    public void updateActiveDays(int id, int daysToUpdate){
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            Habit habit = session.get(Habit.class, id);
            habit.setActiveDays(habit.getActiveDays() + daysToUpdate);
            session.persist(habit);
            transaction.commit();
        }
        catch (Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            e.printStackTrace();
        }
        finally {
            session.close();
        }

    }


}
