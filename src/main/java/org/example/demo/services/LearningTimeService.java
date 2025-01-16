package org.example.demo.services;

import org.example.demo.models.Habit;
import org.example.demo.models.LearningTime;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

public class LearningTimeService {
    private final SessionFactory sessionFactory;

    public LearningTimeService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveLearningTime(int time, Habit habit){
        LearningTime learningTime = new LearningTime();
        learningTime.setTime(time);
        learningTime.setDate(LocalDate.now());
        learningTime.setHabit(habit);
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            session.persist(learningTime);
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
    public double getTotalLearningTime(){
        Session session = sessionFactory.openSession();
        double total = 0;
        try{
            String hql = "SELECT SUM(time) FROM LearningTime ";
            total = session.createQuery(hql, Double.class).getSingleResult();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            session.close();
            return total;
        }
    }


}
