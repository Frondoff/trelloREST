package com.uapp.trello.service;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.objects.dto.BoardColumnDto;
import com.uapp.trello.objects.dto.ColumnsSwapDto;
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

    public void createColumn(BoardColumnDto boardColumnDto) {
        BoardColumn boardColumn = new BoardColumn();
        boardColumn.setName(boardColumnDto.getName());
        boardColumn.setPosition(boardColumnRepository.getCountOfColumns() + 1);
        boardColumnRepository.saveBoardColumn(boardColumn);
    }

    public void editColumnName(BoardColumnDto boardColumnDto) {
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnById(boardColumnDto.getId());
        boardColumn.setName(boardColumnDto.getName());
        boardColumnRepository.updateBoardColumn(boardColumn);
    }

    public void changeColumnOrder(ColumnsSwapDto columnsSwapDto) {
        BoardColumn firstBoardColumn = boardColumnRepository.getBoardColumnByPosition(columnsSwapDto.getFirstElementId());
        BoardColumn secondBoardColumn = boardColumnRepository.getBoardColumnByPosition(columnsSwapDto.getSecondElementId());

        int temp = firstBoardColumn.getId();
        firstBoardColumn.setPosition(secondBoardColumn.getPosition());
        secondBoardColumn.setPosition(temp);

        boardColumnRepository.updateBoardColumn(firstBoardColumn);
        boardColumnRepository.updateBoardColumn(secondBoardColumn);
    }

    public void deleteColumn(BoardColumnDto boardColumnDto) {
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnById(boardColumnDto.getId());
        boardColumnRepository.deleteBoardColumn(boardColumn);
        fixColumnsOrder(boardColumnDto.getPosition());
    }

    private void fixColumnsOrder(int position) {
        List<BoardColumn> boardColumns = boardColumnRepository.getColumnsGreaterThanDeleted(position);
        for (BoardColumn boardColumn : boardColumns) {
            boardColumn.setPosition(boardColumn.getPosition() - 1);
            boardColumnRepository.updateBoardColumn(boardColumn);
        }
    }
}
