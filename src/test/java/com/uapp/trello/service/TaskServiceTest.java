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
    private static final int TASK_ID = 1;
    private static final int COLUMN_ID = 1;
    private static final int UNKNOWN_ID = Integer.MAX_VALUE;
    private static final int POSITION = 1;
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
        BoardColumn boardColumn = new BoardColumn(TEST_COLUMN_NAME, POSITION);
        TaskDto taskDto = new TaskDto(TASK_NAME, TASK_DESCRIPTION, date);
        Task task = new Task(TASK_ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);

        when(boardColumnRepository.getBoardColumnById(COLUMN_ID)).thenReturn(boardColumn);
        when(taskRepository.saveTask(task)).thenReturn(task);

        Task actualTask = taskService.createTask(COLUMN_ID, taskDto);

        assertEquals(TASK_ID, actualTask.getId());
        assertEquals(TASK_NAME, actualTask.getName());
        assertEquals(TASK_DESCRIPTION, actualTask.getDescription());
        assertEquals(date, actualTask.getDate());
        assertEquals(POSITION, actualTask.getPosition());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_create_task_should_throw_exception_when_column_not_found() {
        TaskDto taskDto = new TaskDto(TASK_NAME, TASK_DESCRIPTION, date);

        when(boardColumnRepository.getBoardColumnById(UNKNOWN_ID)).thenReturn(null);

        taskService.createTask(UNKNOWN_ID, taskDto);
    }

    @Test
    public void test_edit_task_was_success() {
        BoardColumn boardColumn = new BoardColumn(COLUMN_ID, TEST_COLUMN_NAME, POSITION);
        Task task = new Task(TASK_ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);
        TaskDto taskDto = new TaskDto(NEW_TASK_NAME, NEW_TASK_DESCRIPTION, NEW_TASK_DATE);

        when(taskRepository.getTaskById(TASK_ID)).thenReturn(task);
        when(taskRepository.updateTask(task)).thenReturn(task);

        Task actualTask = taskService.editTask(TASK_ID, taskDto);

        assertEquals(NEW_TASK_NAME, actualTask.getName());
        assertEquals(NEW_TASK_DESCRIPTION, actualTask.getDescription());
        assertEquals(NEW_TASK_DATE, actualTask.getDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_edit_task_should_throw_exception_when_column_not_found() {
        TaskDto taskDto = new TaskDto(NEW_TASK_NAME, NEW_TASK_DESCRIPTION, NEW_TASK_DATE);

        when(taskRepository.getTaskById(UNKNOWN_ID)).thenReturn(null);

        taskService.editTask(UNKNOWN_ID, taskDto);
    }

    @Test
    public void test_delete_task_was_success() {
        BoardColumn boardColumn = new BoardColumn(COLUMN_ID, TEST_COLUMN_NAME, POSITION);
        Task task = new Task(TASK_ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);
        List<Task> tasks = Arrays.asList(
                new Task(2, TASK_NAME, TASK_DESCRIPTION, date, 2, boardColumn),
                new Task(3, TASK_NAME, TASK_DESCRIPTION, date, 3, boardColumn));

        when(taskRepository.getTaskById(task.getId())).thenReturn(task);
        when(boardColumnRepository.getBoardColumnById(boardColumn.getId())).thenReturn(boardColumn);
        when(taskRepository.getTasksGreaterThanDeleted(COLUMN_ID, POSITION)).thenReturn(tasks);

        List<Task> actualTasks = taskService.deleteTask(COLUMN_ID, TASK_ID);

        assertEquals(1, actualTasks.get(0).getPosition());
        assertEquals(2, actualTasks.get(1).getPosition());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_delete_task_should_throw_exception_when_task_not_found() {
        when(taskRepository.getTaskById(UNKNOWN_ID)).thenReturn(null);

        taskService.deleteTask(COLUMN_ID, UNKNOWN_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_delete_task_should_throw_exception_when_task_column_not_found() {
        BoardColumn boardColumn = new BoardColumn(COLUMN_ID, TEST_COLUMN_NAME, POSITION);
        Task task = new Task(TASK_ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);

        when(taskRepository.getTaskById(TASK_ID)).thenReturn(task);
        when(boardColumnRepository.getBoardColumnById(UNKNOWN_ID)).thenReturn(null);

        taskService.deleteTask(UNKNOWN_ID, TASK_ID);
    }

    @Test
    public void test_change_tasks_order_was_success() {
        BoardColumn boardColumn = new BoardColumn(COLUMN_ID, TEST_COLUMN_NAME, POSITION);
        Task firstTask = new Task(1, TASK_NAME, TASK_DESCRIPTION, date, 1, boardColumn);
        Task secondTask = new Task(2, TASK_NAME, TASK_DESCRIPTION, date, 2, boardColumn);

        when(taskRepository.getTaskById(firstTask.getId())).thenReturn(firstTask);
        when(taskRepository.getTaskByColumnAndPosition(COLUMN_ID, 2)).thenReturn(secondTask);
        when(taskRepository.updateTask(firstTask)).thenReturn(firstTask);
        when(taskRepository.updateTask(secondTask)).thenReturn(secondTask);

        Task actualTask = taskService.changeTasksOrder(COLUMN_ID, TASK_ID, 2);

        assertEquals(2, actualTask.getPosition());
        assertEquals(1, secondTask.getPosition());
        assertEquals(2, firstTask.getPosition());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_change_tasks_order_should_throw_exception_when_task_not_found() {
        when(taskRepository.getTaskById(UNKNOWN_ID)).thenReturn(null);

        taskService.changeTasksOrder(COLUMN_ID, UNKNOWN_ID, POSITION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_change_tasks_order_should_throw_exception_when_task_column_not_found() {
        BoardColumn boardColumn = new BoardColumn(COLUMN_ID, TEST_COLUMN_NAME, POSITION);
        Task task = new Task(TASK_ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);

        when(taskRepository.getTaskById(TASK_ID)).thenReturn(task);
        when(taskRepository.getTaskByColumnAndPosition(UNKNOWN_ID, POSITION)).thenReturn(null);

        taskService.changeTasksOrder(UNKNOWN_ID, TASK_ID, POSITION);
    }

    @Test
    public void test_move_task_to_another_column_was_success() {
        BoardColumn firstBoardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        BoardColumn secondBoardColumn = new BoardColumn(2, TEST_COLUMN_NAME, 2);
        Task task = new Task(TASK_ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, firstBoardColumn);

        when(taskRepository.getTaskById(TASK_ID)).thenReturn(task);
        when(boardColumnRepository.getBoardColumnById(secondBoardColumn.getId())).thenReturn(secondBoardColumn);
        when(taskRepository.updateTask(task)).thenReturn(task);

        Task actualTask = taskService.moveTaskToAnotherColumn(COLUMN_ID, TASK_ID, 2);

        assertEquals(2, actualTask.getBoardColumn().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_move_task_to_another_column_should_throw_exception_when_task_not_found() {
        when(taskRepository.getTaskById(UNKNOWN_ID)).thenReturn(null);

        taskService.moveTaskToAnotherColumn(COLUMN_ID, UNKNOWN_ID, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_move_task_to_another_column_should_throw_exception_when_new_task_column_not_found() {
        BoardColumn boardColumn = new BoardColumn(COLUMN_ID, TEST_COLUMN_NAME, POSITION);
        Task task = new Task(TASK_ID, TASK_NAME, TASK_DESCRIPTION, date, POSITION, boardColumn);

        when(taskRepository.getTaskById(TASK_ID)).thenReturn(task);
        when(boardColumnRepository.getBoardColumnById(UNKNOWN_ID)).thenReturn(null);

        taskService.moveTaskToAnotherColumn(COLUMN_ID, TASK_ID, UNKNOWN_ID);
    }
}