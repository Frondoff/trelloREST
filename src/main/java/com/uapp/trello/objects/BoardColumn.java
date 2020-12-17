package com.uapp.trello.objects;

import javax.persistence.*;

@Entity
@Table(name = "board_column")
public class BoardColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private int position;

    public BoardColumn() {
    }

    public BoardColumn(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public BoardColumn(int id, String name, int position) {
        this.id = id;
        this.name = name;
        this.position = position;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BoardColumn boardColumn = (BoardColumn) obj;
        if (name != null && position > 0) {
            return boardColumn.id == id && boardColumn.name.equals(name) && boardColumn.position == position;
        } else return false;
    }
}
