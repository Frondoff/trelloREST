package com.uapp.trello.service;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.repository.BoardColumnRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class BoardColumnServiceTest {

    private static final String TEST_COLUMN_NAME = "TestColumn";
    private static final String NEW_TEST_COLUMN_NAME = "New TestColumn";
    private static final int UNKNOWN_ID = Integer.MAX_VALUE;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @InjectMocks
    private BoardColumnService boardColumnService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        boardColumnService = new BoardColumnService(boardColumnRepository);
    }

    @Test
    public void test_create_column_was_success() {
        BoardColumn boardColumn = new BoardColumn(TEST_COLUMN_NAME, 1);

        when(boardColumnRepository.saveBoardColumn(boardColumn)).thenReturn(new BoardColumn(1, TEST_COLUMN_NAME, 1));

        BoardColumn actualColumn = boardColumnService.createColumn(TEST_COLUMN_NAME);

        assertEquals(1, actualColumn.getId());
        assertEquals(TEST_COLUMN_NAME, actualColumn.getName());
        assertEquals(1, actualColumn.getPosition());
    }

    @Test
    public void test_edit_column_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);

        when(boardColumnRepository.getBoardColumnById(1)).thenReturn(boardColumn);
        when(boardColumnRepository.updateBoardColumn(boardColumn)).thenReturn(boardColumn);

        BoardColumn actualColumn = boardColumnService.editColumn(1, NEW_TEST_COLUMN_NAME);

        assertEquals(NEW_TEST_COLUMN_NAME, actualColumn.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_edit_column_should_throw_exception_when_column_not_found() {
        when(boardColumnRepository.getBoardColumnById(UNKNOWN_ID)).thenReturn(null);

        boardColumnService.editColumn(UNKNOWN_ID, NEW_TEST_COLUMN_NAME);
    }

    @Test
    public void test_change_columns_order_was_success() {
        BoardColumn firstBoardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        BoardColumn secondBoardColumn = new BoardColumn(2, TEST_COLUMN_NAME, 2);

        when(boardColumnRepository.getBoardColumnById(firstBoardColumn.getId())).thenReturn(firstBoardColumn);
        when(boardColumnRepository.getBoardColumnByPosition(secondBoardColumn.getId())).thenReturn(secondBoardColumn);
        when(boardColumnRepository.updateBoardColumn(firstBoardColumn)).thenReturn(firstBoardColumn);
        when(boardColumnRepository.updateBoardColumn(secondBoardColumn)).thenReturn(secondBoardColumn);

        BoardColumn actualColumn = boardColumnService.changeColumnOrder(1, 2);

        assertEquals(2, actualColumn.getPosition());
        assertEquals(1, secondBoardColumn.getPosition());
        assertEquals(2, firstBoardColumn.getPosition());
    }

    @Test
    public void test_delete_column_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        List<BoardColumn> boardColumns = Arrays.asList(
                new BoardColumn(2, TEST_COLUMN_NAME, 2),
                new BoardColumn(3, TEST_COLUMN_NAME, 3));

        when(boardColumnRepository.getBoardColumnById(boardColumn.getId())).thenReturn(boardColumn);
        when(boardColumnRepository.getColumnsGreaterThanDeleted(boardColumn.getPosition())).thenReturn(boardColumns);

        List<BoardColumn> actualBoardColumns = boardColumnService.deleteColumn(boardColumn.getId());

        assertEquals(1, actualBoardColumns.get(0).getPosition());
        assertEquals(2, actualBoardColumns.get(1).getPosition());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_delete_column_should_throw_exception_when_column_not_found() {
        when(boardColumnRepository.getBoardColumnById(UNKNOWN_ID)).thenReturn(null);

        boardColumnService.deleteColumn(UNKNOWN_ID);
    }
}
