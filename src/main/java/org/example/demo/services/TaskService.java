package org.example.demo.services;

import org.example.demo.enums.Status;
import org.example.demo.enums.TaskType;
import org.example.demo.models.Habit;
import org.example.demo.models.Task;
import org.example.demo.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {

    private final SessionFactory sessionFactory;

    public TaskService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addNewTask(Task task){
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            session.persist(task);
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

    public List<Task> getUpcomingDeadlineTasks(){
        List<Task> tasks = new ArrayList<>();
        Session session = sessionFactory.openSession();

        try{
            tasks = session.createQuery("FROM Task WHERE type > 0 and status = 0 order by date LIMIT 5", Task.class)
                    .getResultList();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
        }
        return tasks;
    }
    public long getTotalNumberCompletedTasks(){
        long total = 0;
        Session session = sessionFactory.openSession();
        try{
            total = session.createQuery("SELECT COUNT(t.id) FROM Task t WHERE t.status > 0 AND t.type = 0", Long.class)
                    .getSingleResult();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
        }
        return total;
    }
    public long getTotalNumberTasks(){
        long total = 0;
        Session session = sessionFactory.openSession();
        try{
            total = session.createQuery("SELECT COUNT(t.id) FROM Task t WHERE t.type = 0", Long.class)
                    .getSingleResult();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
        }
        return total;
    }

    public List<Task> getAllTasks(){
        List<Task> tasks = new ArrayList<>();
        Session session = sessionFactory.openSession();

        try{
            tasks = session.createQuery("FROM Task", Task.class).getResultList();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
        }
        return tasks;
    }
    public List<LocalDate> convertToTaskDateList(List<Task> tasks){
        return tasks.stream().map(Task::getDate).collect(Collectors.toList());
    }

    public List<Task> getDeadlineTasksByStatus(Status status){
        List<Task> tasks = new ArrayList<>();
        Session session = sessionFactory.openSession();

        try{
            tasks = session.createQuery("FROM Task WHERE type > 0 and status = :status order by date asc ", Task.class)
                    .setParameter("status", status)
                    .getResultList();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
        }
        return tasks;
    }

    public List<Task> getDailyTasksByDate(LocalDate date) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<Task> taskList = new ArrayList<>();
        try {
            transaction = session.beginTransaction();
            taskList = session.createQuery("FROM Task WHERE date = :date AND type = 0", Task.class)
                                .setParameter("date", date)
                                .getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return taskList;
    }

    public List<Task> getDeadlineTasksByDate(LocalDate date) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<Task> taskList = new ArrayList<>();
        try {
            transaction = session.beginTransaction();
            taskList = session.createQuery("FROM Task WHERE date = :date AND type > 0", Task.class)
                    .setParameter("date", date)
                    .getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return taskList;
    }



    public void deleteTask(Task task){
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            session.remove(task);
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

    public void changeTaskStatus(Task task, Status status){
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            task.setStatus(status);
            session.update(task);
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
    public int getCompletedTasksOfHabit(List<Task> tasks, Habit habit){
        return (int) tasks.stream()
                        .filter(task -> task.getHabit().equals(habit))
                        .filter(task -> task.getStatus() == Status.COMPLETE)
                        .count();
    }




}
