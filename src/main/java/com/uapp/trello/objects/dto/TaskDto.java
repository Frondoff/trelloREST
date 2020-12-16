package com.uapp.trello.objects.dto;

public class TaskDto {

    private int id;
    private String name;
    private String description;
    private String date;
    private int position;
    private int boardColumnId;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getBoardColumnId() {
        return boardColumnId;
    }

    public void setBoardColumnId(int boardColumnId) {
        this.boardColumnId = boardColumnId;
    }
}
