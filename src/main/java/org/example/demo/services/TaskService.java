package org.example.demo.services;

import org.example.demo.enums.Status;
import org.example.demo.models.Task;
import org.example.demo.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public List<Task> getTasksByDate(LocalDate date) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<Task> taskList = new ArrayList<>();
        try {
            transaction = session.beginTransaction();
            taskList = session.createQuery("FROM Task WHERE date = :date", Task.class)
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
            task.setStatus(status.getCode());
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




}
