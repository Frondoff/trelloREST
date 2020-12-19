package com.uapp.trello.repository;

import com.uapp.trello.objects.BoardColumn;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class BoardColumnRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public BoardColumnRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public BoardColumn saveBoardColumn(BoardColumn boardColumn) {
        Session session = sessionFactory.getCurrentSession();
        session.save(boardColumn);

        return boardColumn;
    }

    public BoardColumn updateBoardColumn(BoardColumn boardColumn) {
        Session session = sessionFactory.getCurrentSession();
        session.update(boardColumn);

        return boardColumn;
    }

    public void deleteBoardColumn(BoardColumn boardColumn) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(boardColumn);
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

    public Long getCountOfColumns() {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT count(distinct bc.id) FROM BoardColumn bc", Long.class)
                .getSingleResult();
    }

    public List<BoardColumn> getColumnsGreaterThanDeleted(int position) {
        return sessionFactory.getCurrentSession()
                .createQuery("select bc from BoardColumn bc where bc.position > :position", BoardColumn.class)
                .setParameter("position", position)
                .list();
    }
}
