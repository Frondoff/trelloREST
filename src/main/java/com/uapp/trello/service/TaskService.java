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

    public void createTask(TaskDto taskDto) {
        Task task = new Task();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setDate(String.valueOf(LocalDate.now()));
        task.setPosition(taskDto.getPosition());
        task.setBoardColumn(taskDto.getBoardColumn());
        taskRepository.saveTask(task);
    }

    public void editTask(TaskDto taskDto) {
        Task task = taskRepository.getTaskById(taskDto.getId());
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setDate(taskDto.getDate());
        taskRepository.updateTask(task);
    }

    public void deleteTask(TaskDto taskDto) {
        Task task = taskRepository.getTaskById(taskDto.getId());
        taskRepository.deleteTask(task);

        fixTasksOrder(taskDto.getBoardColumn().getId(), taskDto.getPosition());
    }

    public void changeTasksOrder(TaskDto taskDto, int newPosition) {

        Task firstTask = taskRepository.getTaskByColumnAndPosition(taskDto.getBoardColumn().getId(),
                taskDto.getPosition());
        Task secondTask = taskRepository.getTaskByColumnAndPosition(taskDto.getBoardColumn().getId(),
                newPosition);

        int temp = firstTask.getPosition();
        firstTask.setPosition(secondTask.getPosition());
        secondTask.setPosition(temp);

        taskRepository.updateTask(firstTask);
        taskRepository.updateTask(secondTask);
    }

    public void moveTaskToAnotherColumn(int taskId, int newColumnPosition) {
        Task task = taskRepository.getTaskById(taskId);
        BoardColumn boardColumn = boardColumnRepository.getBoardColumnByPosition(newColumnPosition);
        task.setBoardColumn(boardColumn);
        task.setPosition(taskRepository.getLastReservedPosition(newColumnPosition) + 1);
        taskRepository.updateTask(task);
        fixTasksOrder(task.getBoardColumn().getId(), task.getPosition());
    }

    private void fixTasksOrder(int columnId, int position) {
        List<Task> tasks = taskRepository.getTasksGreaterThanDeleted(columnId, position);
        for (Task task : tasks) {
            task.setPosition(task.getPosition() - 1);
            taskRepository.updateTask(task);
        }
    }
}
