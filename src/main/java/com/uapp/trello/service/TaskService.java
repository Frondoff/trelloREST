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

    public Task createTask(TaskDto taskDto) {
        Task task = new Task();
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnById(taskDto.getBoardColumnId());

        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setDate(String.valueOf(LocalDate.now()));
        task.setPosition((int) (taskRepository.getCountOfTasks(boardColumn.getId()) + 1));
        task.setBoardColumn(boardColumn);

        return taskRepository.saveTask(task);
    }

    public Task editTask(int id, TaskDto taskDto) {
        Task task = taskRepository.getTaskById(id);
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setDate(taskDto.getDate());

        return taskRepository.updateTask(task);
    }

    public void deleteTask(int id) {
        Task task = taskRepository.getTaskById(id);
        taskRepository.deleteTask(task);

        fixTasksOrder(task.getBoardColumn().getId(), task.getPosition());
    }

    public Task changeTasksOrder(int id, int newPosition) {

        Task firstTask = taskRepository.getTaskById(id);
        Task secondTask = taskRepository.getTaskByColumnAndPosition(firstTask.getBoardColumn().getId(), newPosition);

        secondTask.setPosition(firstTask.getPosition());
        firstTask.setPosition(newPosition);

        taskRepository.updateTask(secondTask);
        return taskRepository.updateTask(firstTask);
    }

    public Task moveTaskToAnotherColumn(int taskId, int newColumnId) {
        Task task = taskRepository.getTaskById(taskId);
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnById(newColumnId);
        int oldColumnId = task.getBoardColumn().getId();
        int oldPosition = task.getPosition();

        task.setBoardColumn(boardColumn);
        //if (boardC)
        task.setPosition(taskRepository.getLastReservedPosition(newColumnId) + 1);

        fixTasksOrder(oldColumnId, oldPosition);
        return taskRepository.updateTask(task);
    }

    private void fixTasksOrder(int columnId, int position) {
        List<Task> tasks = taskRepository.getTasksGreaterThanDeleted(columnId, position);
        for (Task task : tasks) {
            task.setPosition(task.getPosition() - 1);
            taskRepository.updateTask(task);
        }
    }
}
