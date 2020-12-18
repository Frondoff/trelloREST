package com.uapp.trello.controllers;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.objects.Task;
import com.uapp.trello.objects.dto.TaskDto;
import com.uapp.trello.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    private static final String TASK_NAME = "task";
    private static final String TASK_DESCRIPTION = "description";
    private static final String TEST_COLUMN_NAME = "TestColumn";
    private static final int ID = 1;
    private static final int UNKNOWN_ID = Integer.MAX_VALUE;
    private static final int POSITION = 1;
    private String date;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @BeforeEach
    public void init() {
        date = String.valueOf(LocalDate.now());
    }

    @Test
    public void test_create_task_was_success() throws Exception {
        BoardColumn boardColumn = new BoardColumn(ID, TEST_COLUMN_NAME, POSITION);
        TaskDto taskDto = new TaskDto(TASK_NAME, TASK_DESCRIPTION, date);
        Task task = new Task(ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);

        when(taskService.createTask(any(int.class), any(TaskDto.class))).thenReturn(task);

        mockMvc.perform(post("/columns/{columnID}/tasks", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDto.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(TASK_NAME)))
                .andExpect(jsonPath("$.description", is(TASK_DESCRIPTION)))
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.position", is(POSITION)))
                .andExpect(jsonPath("$.boardColumn.id", is(ID)))
                .andExpect(jsonPath("$.boardColumn.name", is(TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.boardColumn.position", is(POSITION)));
    }

    @Test
    public void test_edit_task_was_success() throws Exception {
        BoardColumn boardColumn = new BoardColumn(ID, TEST_COLUMN_NAME, POSITION);
        TaskDto taskDto = new TaskDto(TASK_NAME, TASK_DESCRIPTION, date);
        Task task = new Task(ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);

        when(taskService.editTask(any(int.class), any(TaskDto.class))).thenReturn(task);

        mockMvc.perform(put("/columns/**/tasks/{taskId}", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDto.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(TASK_NAME)))
                .andExpect(jsonPath("$.description", is(TASK_DESCRIPTION)))
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.position", is(POSITION)))
                .andExpect(jsonPath("$.boardColumn.id", is(ID)))
                .andExpect(jsonPath("$.boardColumn.name", is(TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.boardColumn.position", is(POSITION)));
    }

    @Test
    public void test_edit_column_with_unknown_id_will_return_404_not_found() throws Exception {
        TaskDto taskDto = new TaskDto(TASK_NAME, TASK_DESCRIPTION, date);
        when(taskService.editTask(any(int.class), any(TaskDto.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/columns/**/tasks/{taskId}", UNKNOWN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDto.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_change_tasks_order_was_success() throws Exception {
        BoardColumn boardColumn = new BoardColumn(ID, TEST_COLUMN_NAME, POSITION);
        Task task = new Task(ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);

        when(taskService.changeTasksOrder(ID, ID, POSITION)).thenReturn(task);

        mockMvc.perform(put("/columns/{columnId}/tasks/{taskId}/order", ID, ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(POSITION)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(TASK_NAME)))
                .andExpect(jsonPath("$.description", is(TASK_DESCRIPTION)))
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.position", is(POSITION)))
                .andExpect(jsonPath("$.boardColumn.id", is(ID)))
                .andExpect(jsonPath("$.boardColumn.name", is(TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.boardColumn.position", is(POSITION)));
    }

    @Test
    public void test_change_tasks_order_with_unknown_id_will_return_404_not_found() throws Exception {
        when(taskService.changeTasksOrder(UNKNOWN_ID, UNKNOWN_ID, POSITION)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/columns/{columnId}/tasks/{taskId}/order", UNKNOWN_ID, UNKNOWN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(POSITION)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_move_task_to_another_column_was_success() throws Exception {
        BoardColumn boardColumn = new BoardColumn(ID, TEST_COLUMN_NAME, POSITION);
        Task task = new Task(ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);

        when(taskService.moveTaskToAnotherColumn(ID, ID, ID)).thenReturn(task);

        mockMvc.perform(put("/columns/{columnId}/tasks/{taskId}/move", ID, ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ID)))
                .andExpect(jsonPath("$.name", is(TASK_NAME)))
                .andExpect(jsonPath("$.description", is(TASK_DESCRIPTION)))
                .andExpect(jsonPath("$.date", is(date)))
                .andExpect(jsonPath("$.position", is(POSITION)))
                .andExpect(jsonPath("$.boardColumn.id", is(ID)))
                .andExpect(jsonPath("$.boardColumn.name", is(TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.boardColumn.position", is(POSITION)));
    }

    @Test
    public void test_move_task_to_another_column_with_unknown_id_will_return_404_not_found() throws Exception {
        when(taskService.moveTaskToAnotherColumn(UNKNOWN_ID, UNKNOWN_ID, UNKNOWN_ID)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(put("/columns/{columnId}/tasks/{taskId}/move", UNKNOWN_ID, UNKNOWN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(UNKNOWN_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_delete_task_was_success() throws Exception {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        List<Task> tasks = Arrays.asList(
                new Task(2, TASK_NAME, TASK_DESCRIPTION, date, 2, boardColumn),
                new Task(3, TASK_NAME, TASK_DESCRIPTION, date, 3, boardColumn));

        when(taskService.deleteTask(ID, ID)).thenReturn(tasks);

        mockMvc.perform(
                delete("/columns/{columnId}/tasks/{taskId}", ID, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(2)))
                .andExpect(jsonPath("$.[0].name", is(TASK_NAME)))
                .andExpect(jsonPath("$.[0].description", is(TASK_DESCRIPTION)))
                .andExpect(jsonPath("$.[0].date", is(date)))
                .andExpect(jsonPath("$.[0].position", is(2)))
                .andExpect(jsonPath("$.[0].boardColumn.id", is(ID)))
                .andExpect(jsonPath("$.[0].boardColumn.name", is(TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.[0].boardColumn.position", is(POSITION)))
                .andExpect(jsonPath("$.[1].id", is(3)))
                .andExpect(jsonPath("$.[1].name", is(TASK_NAME)))
                .andExpect(jsonPath("$.[1].description", is(TASK_DESCRIPTION)))
                .andExpect(jsonPath("$.[1].date", is(date)))
                .andExpect(jsonPath("$.[1].position", is(3)))
                .andExpect(jsonPath("$.[1].boardColumn.id", is(ID)))
                .andExpect(jsonPath("$.[1].boardColumn.name", is(TEST_COLUMN_NAME)))
                .andExpect(jsonPath("$.[1].boardColumn.position", is(POSITION)));
    }

    @Test
    public void test_delete_task_with_unknown_id_will_return_404_not_found() throws Exception {
        when(taskService.deleteTask(ID, UNKNOWN_ID)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(
                delete("/columns/{columnId}/tasks/{taskId}", ID, UNKNOWN_ID))
                .andExpect(status().isNotFound());
    }
}
