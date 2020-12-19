package com.uapp.trello.controllers;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.service.BoardColumnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardColumnController.class)
public class BoardColumnControllerTest {

    private static final String TEST_COLUMN_NAME = "TestColumn";
    private static final String NEW_TEST_COLUMN_NAME = "New TestColumn";
    private static final int ID = 1;
    private static final int UNKNOWN_ID = Integer.MAX_VALUE;
    private static final int POSITION = 1;
    private static final int NUMBER_OF_INVOCATIONS = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardColumnService boardColumnService;

    @Test
    public void test_create_column_was_success() throws Exception {
        BoardColumn boardColumn = new BoardColumn(ID, TEST_COLUMN_NAME, POSITION);
        when(boardColumnService.createColumn(TEST_COLUMN_NAME)).thenReturn(boardColumn);

        mockMvc.perform(post("/columns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TEST_COLUMN_NAME))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.position", is(POSITION)));

        verify(boardColumnService, times(NUMBER_OF_INVOCATIONS)).createColumn(TEST_COLUMN_NAME);
        verifyNoMoreInteractions(boardColumnService);
    }

    @Test
    public void test_edit_column_was_success() throws Exception {
        BoardColumn boardColumn = new BoardColumn(ID, NEW_TEST_COLUMN_NAME, POSITION);
        when(boardColumnService.editColumn(ID, NEW_TEST_COLUMN_NAME)).thenReturn(boardColumn);

        mockMvc.perform(put("/columns/{id}", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(NEW_TEST_COLUMN_NAME))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(NEW_TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.position", is(POSITION)));

        verify(boardColumnService, times(NUMBER_OF_INVOCATIONS)).editColumn(ID, NEW_TEST_COLUMN_NAME);
        verifyNoMoreInteractions(boardColumnService);
    }

    @Test
    public void test_edit_column_with_unknown_id_will_return_404_not_found() throws Exception {
        when(boardColumnService.editColumn(UNKNOWN_ID, NEW_TEST_COLUMN_NAME)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/columns/{id}", UNKNOWN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(NEW_TEST_COLUMN_NAME))
                .andExpect(status().isNotFound());

        verify(boardColumnService, times(NUMBER_OF_INVOCATIONS)).editColumn(UNKNOWN_ID, NEW_TEST_COLUMN_NAME);
        verifyNoMoreInteractions(boardColumnService);
    }

    @Test
    public void test_change_columns_order_was_success() throws Exception {
        BoardColumn boardColumn = new BoardColumn(ID, TEST_COLUMN_NAME, POSITION);
        when(boardColumnService.changeColumnOrder(ID, POSITION)).thenReturn(boardColumn);

        mockMvc.perform(put("/columns/{id}/move", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(POSITION)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.position", is(POSITION)));

        verify(boardColumnService, times(NUMBER_OF_INVOCATIONS)).changeColumnOrder(ID, POSITION);
        verifyNoMoreInteractions(boardColumnService);
    }

    @Test
    public void test_delete_column_was_success() throws Exception {
        List<BoardColumn> boardColumns = Arrays.asList(
                new BoardColumn(2, TEST_COLUMN_NAME, 1),
                new BoardColumn(3, TEST_COLUMN_NAME, 2));

        when(boardColumnService.deleteColumn(ID)).thenReturn(boardColumns);

        mockMvc.perform(
                delete("/columns/{id}", ID))
                .andExpect(status().isOk());

        verify(boardColumnService, times(NUMBER_OF_INVOCATIONS)).deleteColumn(ID);
        verifyNoMoreInteractions(boardColumnService);
    }

    @Test
    public void test_delete_column_with_unknown_id_will_return_404_not_found() throws Exception {
        when(boardColumnService.deleteColumn(UNKNOWN_ID)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(
                delete("/columns/{id}", UNKNOWN_ID))
                .andExpect(status().isNotFound());

        verify(boardColumnService, times(NUMBER_OF_INVOCATIONS)).deleteColumn(UNKNOWN_ID);
        verifyNoMoreInteractions(boardColumnService);
    }
}