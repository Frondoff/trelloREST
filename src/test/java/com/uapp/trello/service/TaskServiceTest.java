package com.uapp.trello.service;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.objects.Task;
import com.uapp.trello.objects.dto.TaskDto;
import com.uapp.trello.repository.BoardColumnRepository;
import com.uapp.trello.repository.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    private static final String TASK_NAME = "task";
    private static final String NEW_TASK_NAME = "new name";
    private static final String TASK_DESCRIPTION = "description";
    private static final String NEW_TASK_DESCRIPTION = "new description";
    private static final String TEST_COLUMN_NAME = "TestColumn";
    private static final String NEW_TASK_DATE = "2020-12-15";
    private static final int UNKNOWN_ID = Integer.MAX_VALUE;
    private String date;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private BoardColumnRepository boardColumnRepository;
    @InjectMocks
    private TaskService taskService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepository, boardColumnRepository);
        date = String.valueOf(LocalDate.now());
    }

    @Test
    public void test_create_task_was_success() {
        BoardColumn boardColumn = new BoardColumn(TEST_COLUMN_NAME, 1);
        TaskDto taskDto = new TaskDto(1, TASK_NAME, TASK_DESCRIPTION, date, 1, 1);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, date, 1, boardColumn);

        when(boardColumnRepository.getBoardColumnById(taskDto.getBoardColumnId())).thenReturn(boardColumn);
        when(taskRepository.saveTask(task)).thenReturn(task);

        Task actualTask = taskService.createTask(taskDto);

        assertEquals(1, actualTask.getId());
        assertEquals(TASK_NAME, actualTask.getName());
        assertEquals(TASK_DESCRIPTION, actualTask.getDescription());
        assertEquals(date, actualTask.getDate());
        assertEquals(1, actualTask.getPosition());

    }

    @Test
    public void test_edit_task_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, date, 1, boardColumn);
        TaskDto taskDto = new TaskDto(1, NEW_TASK_NAME, NEW_TASK_DESCRIPTION, NEW_TASK_DATE, 1, 1);

        when(taskRepository.getTaskById(taskDto.getId())).thenReturn(task);
        when(taskRepository.updateTask(task)).thenReturn(task);

        Task actualTask = taskService.editTask(1, taskDto);

        assertEquals(NEW_TASK_NAME, actualTask.getName());
        assertEquals(NEW_TASK_DESCRIPTION, actualTask.getDescription());
        assertEquals(NEW_TASK_DATE, actualTask.getDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_edit_task_should_throw_exception_when_column_not_found() {
        TaskDto taskDto = new TaskDto(1, NEW_TASK_NAME, NEW_TASK_DESCRIPTION, NEW_TASK_DATE, 1, 1);

        when(taskRepository.getTaskById(UNKNOWN_ID)).thenReturn(null);

        taskService.editTask(UNKNOWN_ID, taskDto);
    }

    @Test
    public void test_delete_task_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, date, 1, boardColumn);
        List<Task> tasks = Arrays.asList(
                new Task(2, TASK_NAME, TASK_DESCRIPTION, date, 2, boardColumn),
                new Task(3, TASK_NAME, TASK_DESCRIPTION, date, 3, boardColumn));

        when(taskRepository.getTaskById(task.getId())).thenReturn(task);
        when(boardColumnRepository.getBoardColumnById(boardColumn.getId())).thenReturn(boardColumn);
        when(taskRepository.getTasksGreaterThanDeleted(1, 1)).thenReturn(tasks);

        List<Task> actualTasks = taskService.deleteTask(1, 1);

        assertEquals(1, actualTasks.get(0).getPosition());
        assertEquals(2, actualTasks.get(1).getPosition());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_delete_task_should_throw_exception_when_task_not_found() {
        when(taskRepository.getTaskById(UNKNOWN_ID)).thenReturn(null);

        taskService.deleteTask(1, UNKNOWN_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_delete_task_should_throw_exception_when_task_column_not_found() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, date, 1, boardColumn);

        when(taskRepository.getTaskById(task.getId())).thenReturn(task);
        when(boardColumnRepository.getBoardColumnById(UNKNOWN_ID)).thenReturn(null);

        taskService.deleteTask(UNKNOWN_ID, task.getId());
    }

    @Test
    public void test_change_tasks_order_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        Task firstTask = new Task(1, TASK_NAME, TASK_DESCRIPTION, date, 1, boardColumn);
        Task secondTask = new Task(2, TASK_NAME, TASK_DESCRIPTION, date, 2, boardColumn);

        when(taskRepository.getTaskById(firstTask.getId())).thenReturn(firstTask);
        when(taskRepository.getTaskByColumnAndPosition(1, 2)).thenReturn(secondTask);
        when(taskRepository.updateTask(firstTask)).thenReturn(firstTask);
        when(taskRepository.updateTask(secondTask)).thenReturn(secondTask);

        Task actualTask = taskService.changeTasksOrder(1, 1, 2);

        assertEquals(2, actualTask.getPosition());
        assertEquals(1, secondTask.getPosition());
        assertEquals(2, firstTask.getPosition());
    }

    @Test
    public void test_move_task_to_another_column_was_success() {
        BoardColumn firstBoardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        BoardColumn secondBoardColumn = new BoardColumn(2, TEST_COLUMN_NAME, 2);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, date, 1, firstBoardColumn);

        when(taskRepository.getTaskById(task.getId())).thenReturn(task);
        when(boardColumnRepository.getBoardColumnById(2)).thenReturn(secondBoardColumn);
        when(taskRepository.updateTask(task)).thenReturn(task);

        Task actualTask = taskService.moveTaskToAnotherColumn(1, 1, 2);

        assertEquals(2, actualTask.getBoardColumn().getId());
    }
}