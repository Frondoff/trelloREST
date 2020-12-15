package com.uapp.trello.objects;

import javax.persistence.*;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private String date;

    @Column
    private int position;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "board_column_id")
    private BoardColumn boardColumn;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public BoardColumn getBoardColumn() {
        return boardColumn;
    }

    public void setBoardColumn(BoardColumn boardColumn) {
        this.boardColumn = boardColumn;
    }
}
