package org.example.demo.services;

import org.example.demo.models.Habit;
import org.example.demo.models.LearningTime;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;

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
}
