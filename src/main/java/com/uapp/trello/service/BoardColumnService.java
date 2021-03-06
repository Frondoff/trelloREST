package com.uapp.trello.service;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.repository.BoardColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardColumnService {

    private final BoardColumnRepository boardColumnRepository;

    @Autowired
    public BoardColumnService(BoardColumnRepository boardColumnRepository) {
        this.boardColumnRepository = boardColumnRepository;
    }

    public BoardColumn createColumn(String name) {
        BoardColumn boardColumn = new BoardColumn();
        boardColumn.setName(name);
        boardColumn.setPosition((int) (boardColumnRepository.getCountOfColumns() + 1));

        return boardColumnRepository.saveBoardColumn(boardColumn);
    }

    public BoardColumn editColumn(int id, String name) {
        if (boardColumnRepository.getBoardColumnById(id) == null) {
            throw new IllegalArgumentException();
        }
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnById(id);
        boardColumn.setName(name);
        return boardColumnRepository.updateBoardColumn(boardColumn);
    }

    public BoardColumn changeColumnOrder(int id, int newPosition) {
        if (boardColumnRepository.getBoardColumnById(id) == null
                || boardColumnRepository.getBoardColumnByPosition(newPosition) == null) {
            throw new IllegalArgumentException();
        }
        BoardColumn firstBoardColumn = boardColumnRepository.getBoardColumnById(id);
        BoardColumn secondBoardColumn = boardColumnRepository.getBoardColumnByPosition(newPosition);

        secondBoardColumn.setPosition(firstBoardColumn.getPosition());
        firstBoardColumn.setPosition(newPosition);

        boardColumnRepository.updateBoardColumn(secondBoardColumn);
        return boardColumnRepository.updateBoardColumn(firstBoardColumn);
    }

    public List<BoardColumn> deleteColumn(int id) {
        if (boardColumnRepository.getBoardColumnById(id) == null) {
            throw new IllegalArgumentException();
        }
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnById(id);
        boardColumnRepository.deleteBoardColumn(boardColumn);

        return fixColumnsOrder(boardColumnRepository.getColumnsGreaterThanDeleted(boardColumn.getPosition()));
    }

    private List<BoardColumn> fixColumnsOrder(List<BoardColumn> boardColumns) {
        for (BoardColumn boardColumn : boardColumns) {
            boardColumn.setPosition(boardColumn.getPosition() - 1);
            boardColumnRepository.updateBoardColumn(boardColumn);
        }
        return boardColumns;
    }
}
