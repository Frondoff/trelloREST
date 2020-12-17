package com.uapp.trello.service;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.objects.Task;
import com.uapp.trello.objects.dto.TaskDto;
import com.uapp.trello.repository.BoardColumnRepository;
import com.uapp.trello.repository.TaskRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Listeners(MockitoTestNGListener.class)
public class TaskServiceTest {

    private static final String TASK_NAME = "task";
    private static final String NEW_TASK_NAME = "new name";
    private static final String TASK_DESCRIPTION = "description";
    private static final String NEW_TASK_DESCRIPTION = "new description";
    private static final String TEST_COLUMN_NAME = "TestColumn";
    private static final String NEW_TASK_DATE = "2020-12-15";

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private BoardColumnRepository boardColumnRepository;
    @InjectMocks
    private TaskService taskService;

    @Test
    public void test_create_task_was_success() {
        BoardColumn boardColumn = new BoardColumn(TEST_COLUMN_NAME, 1);
        TaskDto taskDto = new TaskDto(1, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 1, 1);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 1, boardColumn);

        Mockito.when(boardColumnRepository.getBoardColumnById(taskDto.getBoardColumnId())).thenReturn(boardColumn);
        Mockito.when(taskRepository.saveTask(task)).thenReturn(task);

        Task expectedTask = taskService.createTask(taskDto);

        assertEquals(task, expectedTask);
    }

    @Test
    public void test_edit_task_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 1, boardColumn);
        TaskDto taskDto = new TaskDto(1, NEW_TASK_NAME, NEW_TASK_DESCRIPTION, NEW_TASK_DATE, 1, 1);

        Mockito.when(taskRepository.getTaskById(taskDto.getId())).thenReturn(task);
        Mockito.when(taskRepository.updateTask(task)).thenReturn(task);

        Task expectedTask = taskService.editTask(1, taskDto);

        assertEquals(NEW_TASK_NAME, expectedTask.getName());
        assertEquals(NEW_TASK_DESCRIPTION, expectedTask.getDescription());
        assertEquals(NEW_TASK_DATE, expectedTask.getDate());
    }

    @Test
    public void test_delete_task_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 1, boardColumn);
        List<Task> tasks = Arrays.asList(
                new Task(2, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 2, boardColumn),
                new Task(3, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 3, boardColumn));

        Mockito.when(taskRepository.getTaskById(task.getId())).thenReturn(task);
        Mockito.when(taskRepository.getTasksGreaterThanDeleted(1, 1)).thenReturn(tasks);

        taskService.deleteTask(1, 1);

        Mockito.verify(taskRepository, Mockito.times(1)).deleteTask(task);
    }

    @Test
    public void test_change_tasks_order_was_success() {
        BoardColumn boardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        Task firstTask = new Task(1, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 1, boardColumn);
        Task secondTask = new Task(2, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 2, boardColumn);

        Mockito.when(taskRepository.getTaskById(firstTask.getId())).thenReturn(firstTask);
        Mockito.when(taskRepository.getTaskByColumnAndPosition(1, 2)).thenReturn(secondTask);
        Mockito.when(taskRepository.updateTask(firstTask)).thenReturn(firstTask);
        Mockito.when(taskRepository.updateTask(secondTask)).thenReturn(secondTask);

        Task expectedTask = taskService.changeTasksOrder(1, 1, 2);

        assertEquals(2, expectedTask.getPosition());
        assertEquals(1, secondTask.getPosition());
        assertEquals(2, firstTask.getPosition());
    }

    @Test
    public void test_move_task_to_another_column_was_success() {
        BoardColumn firstBoardColumn = new BoardColumn(1, TEST_COLUMN_NAME, 1);
        BoardColumn secondBoardColumn = new BoardColumn(2, TEST_COLUMN_NAME, 2);
        Task task = new Task(1, TASK_NAME, TASK_DESCRIPTION, String.valueOf(LocalDate.now()), 1, firstBoardColumn);

        Mockito.when(taskRepository.getTaskById(task.getId())).thenReturn(task);
        Mockito.when(boardColumnRepository.getBoardColumnById(2)).thenReturn(secondBoardColumn);
        Mockito.when(taskRepository.updateTask(task)).thenReturn(task);

        Task expectedTask = taskService.moveTaskToAnotherColumn(1, 1, 2);

        assertEquals(2, expectedTask.getBoardColumn().getId());
    }
}