package com.uapp.trello.service;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.repository.BoardColumnRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class BoardColumnServiceTest {

    private static final String TEST_COLUMN_NAME = "TestColumn";
    @Mock
    private BoardColumnRepository boardColumnRepository;
    @InjectMocks
    private BoardColumnService boardColumnService;

    @Test
    public void test_create_column_was_success() {
        BoardColumn boardColumn = new BoardColumn(TEST_COLUMN_NAME, 1);
        Mockito.when(boardColumnRepository.saveBoardColumn(boardColumn)).thenReturn(boardColumn);

        BoardColumn expectedColumn = boardColumnService.createColumn(TEST_COLUMN_NAME);

        assertEquals(expectedColumn, boardColumn);
    }

    @Test
    public void test_edit_column__was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);

        Mockito.when(boardColumnRepository.getBoardColumnById(1)).thenReturn(boardColumn);
        Mockito.when(boardColumnRepository.updateBoardColumn(boardColumn)).thenReturn(boardColumn);

        BoardColumn expectedColumn = boardColumnService.editColumn(1, "name");

        assertEquals(expectedColumn, boardColumn);
        assertEquals(expectedColumn.getId(), boardColumn.getId());
    }

    @Test
    public void test_change_columns_order_was_success() {
        BoardColumn firstBoardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        BoardColumn secondBoardColumn = new BoardColumn(2, TEST_COLUMN_NAME, 2);

        Mockito.when(boardColumnRepository.getBoardColumnById(1)).thenReturn(firstBoardColumn);
        Mockito.when(boardColumnRepository.getBoardColumnByPosition(2)).thenReturn(secondBoardColumn);
        Mockito.when(boardColumnRepository.updateBoardColumn(firstBoardColumn)).thenReturn(secondBoardColumn);
        Mockito.when(boardColumnRepository.updateBoardColumn(secondBoardColumn)).thenReturn(firstBoardColumn);

        BoardColumn expectedColumn = boardColumnService.changeColumnOrder(1, 2);

        assertEquals(expectedColumn, secondBoardColumn);
    }

    @Test
    public void test_delete_column_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        List<BoardColumn> boardColumns = Arrays.asList(
                new BoardColumn(2, TEST_COLUMN_NAME, 2),
                new BoardColumn(3, TEST_COLUMN_NAME, 3));
        Mockito.when(boardColumnRepository.getBoardColumnById(1)).thenReturn(boardColumn);
        Mockito.when(boardColumnRepository.getColumnsGreaterThanDeleted(1)).thenReturn(boardColumns);

        boardColumnService.deleteColumn(1);

        Mockito.verify(boardColumnRepository, Mockito.times(1)).deleteBoardColumn(boardColumn);
    }
}
