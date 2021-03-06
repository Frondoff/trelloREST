package com.uapp.trello.repository;

import com.uapp.trello.objects.Task;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class TaskRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TaskRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Task saveTask(Task task) {
        Session session = sessionFactory.getCurrentSession();
        session.save(task);

        return task;
    }

    public Task updateTask(Task task) {
        Session session = sessionFactory.getCurrentSession();
        session.update(task);

        return task;
    }

    public void deleteTask(Task task) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(task);
    }

    public Task getTaskById(int id) {
        return sessionFactory.getCurrentSession()
                .createQuery("Select t from Task t where t.id = :id", Task.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    public Task getTaskByColumnAndPosition(int columnId, int position) {
        return sessionFactory.getCurrentSession()
                .createQuery("Select t from Task t where t.boardColumn.id = :columnId " +
                        "and t.position = :position", Task.class)
                .setParameter("columnId", columnId)
                .setParameter("position", position)
                .uniqueResult();
    }

    public int getLastReservedPosition(int columnId) {
        if (sessionFactory.getCurrentSession()
                .createQuery("Select MAX(t.position) from Task t where t.boardColumn.id = :columnId ")
                .setParameter("columnId", columnId)
                .getSingleResult() == null) {
            return 0;
        } else {
            return (int) sessionFactory.getCurrentSession()
                    .createQuery("Select MAX(t.position) from Task t where t.boardColumn.id = :columnId")
                    .setParameter("columnId", columnId)
                    .getSingleResult();
        }
    }

    public Long getCountOfTasksByColumnId(int columnId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT count(distinct t.id) FROM Task t where t.boardColumn.id = :columnId", Long.class)
                .setParameter("columnId", columnId)
                .getSingleResult();
    }

    public List<Task> getTasksGreaterThanDeleted(int columnId, int position) {
        return sessionFactory.getCurrentSession()
                .createQuery("Select t from Task t where t.boardColumn.id = :columnId " +
                        "and t.position > :position", Task.class)
                .setParameter("columnId", columnId)
                .setParameter("position", position)
                .list();
    }
}
