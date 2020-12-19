package com.uapp.trello.repository;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.objects.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SpringBootTest()
public class TaskRepositoryTest {

    private static final String TASK_NAME = "task";
    private static final String NEW_TASK_NAME = "new name";
    private static final String TASK_DESCRIPTION = "description";
    private static final String NEW_TASK_DESCRIPTION = "new description";
    private static final String DATE = "2020-12-19";
    private static final String TEST_COLUMN_NAME = "TestColumn";
    private static final String NEW_TASK_DATE = "2020-12-15";
    private static final int POSITION = 0;

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    BoardColumnRepository boardColumnRepository;

    @Test
    @Transactional
    public void test_save_task_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(TEST_COLUMN_NAME, POSITION));
        Task expectedTask = new Task(TASK_NAME, TASK_DESCRIPTION, DATE, POSITION, boardColumn);

        Task actualTask = taskRepository.saveTask(expectedTask);

        assertEquals(expectedTask, actualTask);
    }

    @Test
    @Transactional
    public void test_update_task_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(TEST_COLUMN_NAME, POSITION));
        Task expectedTask = new Task(TASK_NAME, TASK_DESCRIPTION, DATE, POSITION, boardColumn);

        expectedTask.setName(NEW_TASK_NAME);
        expectedTask.setDescription(NEW_TASK_DESCRIPTION);
        expectedTask.setDate(NEW_TASK_DATE);
        expectedTask.setPosition(taskRepository.getLastReservedPosition(boardColumn.getId()) + 1);

        Task actualTask = taskRepository.updateTask(expectedTask);

        assertEquals(expectedTask, actualTask);
    }

    @Test
    @Transactional
    public void test_delete_task_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(TEST_COLUMN_NAME, POSITION));
        Task expectedTask = new Task(TASK_NAME, TASK_DESCRIPTION, DATE, POSITION, boardColumn);

        taskRepository.saveTask(expectedTask);
        long countOfTasksBeforeDelete = taskRepository.getCountOfTasksByColumnId(expectedTask.getBoardColumn().getId());

        taskRepository.deleteTask(expectedTask);
        long countOfTasksAfterDelete = taskRepository.getCountOfTasksByColumnId(expectedTask.getBoardColumn().getId());

        assertNotEquals(countOfTasksBeforeDelete, countOfTasksAfterDelete);
    }

    @Test
    @Transactional
    public void test_get_task_by_id_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(TEST_COLUMN_NAME, POSITION));
        Task expectedTask = new Task(TASK_NAME, TASK_DESCRIPTION, DATE, POSITION, boardColumn);
        taskRepository.saveTask(expectedTask);

        Task actualTask = taskRepository.getTaskById(expectedTask.getId());

        assertEquals(expectedTask, actualTask);
    }

    @Test
    @Transactional
    public void test_get_task_by_column_and_position_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(TEST_COLUMN_NAME, POSITION));
        Task expectedTask = new Task(TASK_NAME, TASK_DESCRIPTION, DATE, POSITION, boardColumn);
        taskRepository.saveTask(expectedTask);

        Task actualTask = taskRepository.getTaskByColumnAndPosition(expectedTask.getBoardColumn().getId(), expectedTask.getPosition());

        assertEquals(expectedTask, actualTask);
    }

    @Test
    @Transactional
    public void test_get_last_reserved_position_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(TEST_COLUMN_NAME, POSITION));
        Task expectedTask = new Task(TASK_NAME, TASK_DESCRIPTION, DATE, POSITION, boardColumn);
        taskRepository.saveTask(expectedTask);

        int lastReservedPosition = taskRepository.getLastReservedPosition(boardColumn.getId());
        expectedTask.setPosition(lastReservedPosition + 1);
        taskRepository.saveTask(expectedTask);

        assertNotEquals(lastReservedPosition, taskRepository.getLastReservedPosition(expectedTask.getBoardColumn().getId()));
    }

    @Test
    @Transactional
    public void test_get_last_reserved_position_in_column_without_tasks_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(
                new BoardColumn(TEST_COLUMN_NAME, Math.toIntExact(boardColumnRepository.getCountOfColumns() + 1)));

        assertEquals(0, taskRepository.getLastReservedPosition(boardColumn.getId()));
    }

    @Test
    @Transactional
    public void test_get_count_of_tasks_by_column_id_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(TEST_COLUMN_NAME, POSITION));
        Task expectedTask = new Task(TASK_NAME, TASK_DESCRIPTION, DATE, POSITION, boardColumn);

        taskRepository.saveTask(expectedTask);
        int countOfTasksBeforeDelete = Math.toIntExact(taskRepository.getCountOfTasksByColumnId(expectedTask.getBoardColumn().getId()));

        taskRepository.deleteTask(expectedTask);
        int countOfTasksAfterDelete = Math.toIntExact(taskRepository.getCountOfTasksByColumnId(expectedTask.getBoardColumn().getId()));

        assertNotEquals(countOfTasksBeforeDelete, countOfTasksAfterDelete);
    }

    @Test
    @Transactional
    public void test_get_tasks_greater_than_deleted_was_success() {
        BoardColumn boardColumn = boardColumnRepository.saveBoardColumn(new BoardColumn(TEST_COLUMN_NAME, POSITION));
        Task expectedTask = new Task(TASK_NAME, TASK_DESCRIPTION, DATE, POSITION, boardColumn);

        int firstAvailablePosition = taskRepository.getLastReservedPosition(expectedTask.getBoardColumn().getId()) + 1;

        taskRepository.saveTask(new Task(TASK_NAME, TASK_DESCRIPTION, DATE, firstAvailablePosition, boardColumn));
        taskRepository.saveTask(new Task(TASK_NAME, TASK_DESCRIPTION, DATE, firstAvailablePosition + 1, boardColumn));

        List<Task> tasks = taskRepository.getTasksGreaterThanDeleted(expectedTask.getBoardColumn().getId(), firstAvailablePosition);

        assertEquals(1, tasks.size());
    }
}