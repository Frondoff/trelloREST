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
    private static final String NEW_TEST_COLUMN_NAME = "New TestColumn";

    @Mock
    private BoardColumnRepository boardColumnRepository;
    @InjectMocks
    private BoardColumnService boardColumnService;

    @Test
    public void test_create_column_was_success() {
        BoardColumn boardColumn = new BoardColumn(TEST_COLUMN_NAME, 1);

        Mockito.when(boardColumnRepository.saveBoardColumn(boardColumn)).thenReturn(boardColumn);

        BoardColumn expectedColumn = boardColumnService.createColumn(TEST_COLUMN_NAME);

        assertEquals(boardColumn, expectedColumn);
    }

    @Test
    public void test_edit_column__was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);

        Mockito.when(boardColumnRepository.getBoardColumnById(boardColumn.getId())).thenReturn(boardColumn);
        Mockito.when(boardColumnRepository.updateBoardColumn(boardColumn)).thenReturn(boardColumn);

        BoardColumn expectedColumn = boardColumnService.editColumn(1, NEW_TEST_COLUMN_NAME);

        assertEquals(NEW_TEST_COLUMN_NAME, expectedColumn.getName());
    }

    @Test
    public void test_change_columns_order_was_success() {
        BoardColumn firstBoardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        BoardColumn secondBoardColumn = new BoardColumn(2, TEST_COLUMN_NAME, 2);

        Mockito.when(boardColumnRepository.getBoardColumnById(firstBoardColumn.getId())).thenReturn(firstBoardColumn);
        Mockito.when(boardColumnRepository.getBoardColumnByPosition(secondBoardColumn.getId())).thenReturn(secondBoardColumn);
        Mockito.when(boardColumnRepository.updateBoardColumn(firstBoardColumn)).thenReturn(firstBoardColumn);
        Mockito.when(boardColumnRepository.updateBoardColumn(secondBoardColumn)).thenReturn(secondBoardColumn);

        BoardColumn expectedColumn = boardColumnService.changeColumnOrder(1, 2);

        assertEquals(2, expectedColumn.getPosition());
        assertEquals(1, secondBoardColumn.getPosition());
        assertEquals(2, firstBoardColumn.getPosition());
    }

    @Test
    public void test_delete_column_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        List<BoardColumn> boardColumns = Arrays.asList(
                new BoardColumn(2, TEST_COLUMN_NAME, 2),
                new BoardColumn(3, TEST_COLUMN_NAME, 3));

        Mockito.when(boardColumnRepository.getBoardColumnById(boardColumn.getId())).thenReturn(boardColumn);
        Mockito.when(boardColumnRepository.getColumnsGreaterThanDeleted(boardColumn.getPosition())).thenReturn(boardColumns);

        boardColumnService.deleteColumn(boardColumn.getId());

        Mockito.verify(boardColumnRepository, Mockito.times(1)).deleteBoardColumn(boardColumn);
    }
}
