package com.uapp.trello.repository;

import com.uapp.trello.objects.BoardColumn;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SpringBootTest()
public class BoardColumnRepositoryTest {

    private static final String COLUMN_NAME = "TestColumn";
    private static final String NEW_COLUMN_NAME = "New TestColumn";
    private static final int POSITION = 0;

    @Autowired
    BoardColumnRepository boardColumnRepository;

    @Test
    @Transactional
    public void test_save_board_column_was_success() {
        BoardColumn expectedBoardColumn = new BoardColumn(COLUMN_NAME, POSITION);

        BoardColumn actualBoardColumn = boardColumnRepository.saveBoardColumn(expectedBoardColumn);

        assertEquals(expectedBoardColumn, actualBoardColumn);
    }

    @Test
    @Transactional
    public void test_update_board_column_was_success() {
        BoardColumn expectedBoardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(COLUMN_NAME, POSITION));
        expectedBoardColumn.setName(NEW_COLUMN_NAME);
        expectedBoardColumn.setPosition(0);

        BoardColumn actualBoardColumn = boardColumnRepository.updateBoardColumn(expectedBoardColumn);

        assertEquals(expectedBoardColumn, actualBoardColumn);
    }

    @Test
    @Transactional
    public void test_delete_board_column_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(COLUMN_NAME, POSITION));
        long countOfColumnsBeforeDelete = boardColumnRepository.getCountOfColumns();

        boardColumnRepository.deleteBoardColumn(boardColumn);
        long countOfColumnsAfterDelete = boardColumnRepository.getCountOfColumns();

        assertNotEquals(countOfColumnsBeforeDelete, countOfColumnsAfterDelete);
    }

    @Test
    @Transactional
    public void test_get_board_column_by_id_was_success() {
        BoardColumn expectedBoardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(COLUMN_NAME, POSITION));

        BoardColumn actualBoardColumn = boardColumnRepository.getBoardColumnById(expectedBoardColumn.getId());

        assertEquals(expectedBoardColumn, actualBoardColumn);
    }

    @Test
    @Transactional
    public void test_get_board_column_by_position_was_success() {
        BoardColumn expectedBoardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(COLUMN_NAME, POSITION));

        BoardColumn actualBoardColumn = boardColumnRepository.getBoardColumnByPosition(POSITION);

        assertEquals(expectedBoardColumn, actualBoardColumn);
    }

    @Test
    @Transactional
    public void test_get_count_of_columns_was_success() {
        long countOfColumnsBeforeSaving = boardColumnRepository.getCountOfColumns();

        boardColumnRepository.saveBoardColumn(new BoardColumn(COLUMN_NAME, POSITION));
        long countOfColumnsAfterDelete = boardColumnRepository.getCountOfColumns();

        assertNotEquals(countOfColumnsBeforeSaving, countOfColumnsAfterDelete);
    }

    @Test
    @Transactional
    public void test_get_count_of_columns_greater_than_deleted_was_success() {
        int firstColumnPosition = Math.toIntExact(boardColumnRepository.getCountOfColumns()) + 1;

        boardColumnRepository.saveBoardColumn(new BoardColumn(COLUMN_NAME, firstColumnPosition));
        boardColumnRepository.saveBoardColumn(new BoardColumn(COLUMN_NAME, firstColumnPosition + 1));

        List<BoardColumn> boardColumns = boardColumnRepository.getColumnsGreaterThanDeleted(firstColumnPosition);

        assertEquals(1, boardColumns.size());
    }
}
