package com.uapp.trello.repository;

import com.uapp.trello.objects.BoardColumn;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardColumnRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public BoardColumnRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveBoardColumn(BoardColumn boardColumn) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.save(boardColumn);
        transaction.commit();
    }

    public void updateBoardColumn(BoardColumn boardColumn) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(boardColumn);
        transaction.commit();
    }

    public void deleteBoardColumn(BoardColumn boardColumn) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.delete(boardColumn);
        transaction.commit();
    }

    public BoardColumn getBoardColumnById(int id) {
        return sessionFactory.getCurrentSession()
                .createQuery("Select bc from BoardColumn bc where bc.id = :id", BoardColumn.class)
                .setParameter("id", id)
                .uniqueResult();
    }

    public BoardColumn getBoardColumnByPosition(int position) {
        return sessionFactory.getCurrentSession()
                .createQuery("Select bc from BoardColumn bc where bc.position = :position", BoardColumn.class)
                .setParameter("position", position)
                .uniqueResult();
    }

    public int getCountOfColumns() {
        return (int) sessionFactory.getCurrentSession()
                .createQuery("SELECT count(bc.id) FROM BoardColumn bc")
                .getSingleResult();
    }

    public List<BoardColumn> getColumnsGreaterThanDeleted(int position) {
        return sessionFactory.getCurrentSession()
                .createQuery("select bc from BoardColumn bc where bc.position > :position", BoardColumn.class)
                .setParameter("position", position)
                .list();
    }
}
