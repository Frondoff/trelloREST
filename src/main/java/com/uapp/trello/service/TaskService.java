package com.uapp.trello.service;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.objects.Task;
import com.uapp.trello.objects.dto.TaskDto;
import com.uapp.trello.repository.BoardColumnRepository;
import com.uapp.trello.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    TaskRepository taskRepository;
    BoardColumnRepository boardColumnRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, BoardColumnRepository boardColumnRepository) {
        this.taskRepository = taskRepository;
        this.boardColumnRepository = boardColumnRepository;
    }

    public Task createTask(int columnId, TaskDto taskDto) {
        Task task = new Task();
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnById(columnId);

        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setDate(String.valueOf(LocalDate.now()));
        task.setPosition((int) (taskRepository.getCountOfTasks(boardColumn.getId()) + 1));
        task.setBoardColumn(boardColumn);

        return taskRepository.saveTask(task);
    }

    public Task editTask(int taskId, TaskDto taskDto) {
        if (taskRepository.getTaskById(taskId) == null) {
            throw new IllegalArgumentException();
        }
        Task task = taskRepository.getTaskById(taskId);
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setDate(taskDto.getDate());

        return taskRepository.updateTask(task);
    }

    public List<Task> deleteTask(int columnId, int taskId) {
        if (taskRepository.getTaskById(taskId) == null || boardColumnRepository.getBoardColumnById(columnId) == null) {
            throw new IllegalArgumentException();
        }
        Task task = taskRepository.getTaskById(taskId);
        taskRepository.deleteTask(task);
        return fixTasksOrder(taskRepository.getTasksGreaterThanDeleted(columnId, task.getPosition()));
    }

    public Task changeTasksOrder(int columnId, int taskId, int newPosition) {
        if (taskRepository.getTaskById(taskId) == null
                || taskRepository.getTaskByColumnAndPosition(columnId, newPosition) == null) {
            throw new IllegalArgumentException();
        }
        Task firstTask = taskRepository.getTaskById(taskId);
        Task secondTask = taskRepository.getTaskByColumnAndPosition(columnId, newPosition);

        secondTask.setPosition(firstTask.getPosition());
        firstTask.setPosition(newPosition);

        taskRepository.updateTask(secondTask);

        return taskRepository.updateTask(firstTask);
    }

    public Task moveTaskToAnotherColumn(int columnId, int taskId, int newColumnId) {
        if (taskRepository.getTaskById(taskId) == null
                || boardColumnRepository.getBoardColumnById(newColumnId) == null) {
            throw new IllegalArgumentException();
        }
        Task task = taskRepository.getTaskById(taskId);
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnById(newColumnId);
        int oldPosition = task.getPosition();

        task.setBoardColumn(boardColumn);
        task.setPosition(taskRepository.getLastReservedPosition(newColumnId) + 1);

        List<Task> tasks = taskRepository.getTasksGreaterThanDeleted(columnId, oldPosition);
        fixTasksOrder(tasks);

        return taskRepository.updateTask(task);
    }

    private List<Task> fixTasksOrder(List<Task> tasks) {
        for (Task task : tasks) {
            task.setPosition(task.getPosition() - 1);
            taskRepository.updateTask(task);
        }
        return tasks;
    }
}
