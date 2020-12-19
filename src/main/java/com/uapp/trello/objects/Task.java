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
    private String description;

    @Column
    private String date;

    @Column
    private int position;

    @ManyToOne
    @JoinColumn(name = "board_column_id")
    private BoardColumn boardColumn;

    public Task() {
    }

    public Task(int id, String name, String description, String date, int position, BoardColumn boardColumn) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.position = position;
        this.boardColumn = boardColumn;
    }

    public Task(String name, String description, String date, int position, BoardColumn boardColumn) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.position = position;
        this.boardColumn = boardColumn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Task task = (Task) obj;
        if (name != null && description != null && date != null && position > 0 && boardColumn != null) {
            return task.name.equals(name) && task.description.equals(description) && task.date.equals(date)
                    && task.position == position && task.boardColumn.equals(boardColumn);
        } else return false;
    }
}
