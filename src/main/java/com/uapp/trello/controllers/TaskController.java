package com.uapp.trello.controllers;

import com.uapp.trello.objects.Task;
import com.uapp.trello.objects.dto.TaskDto;
import com.uapp.trello.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/columns/tasks")
    public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto) {
        return new ResponseEntity<>(taskService.createTask(taskDto), HttpStatus.CREATED);
    }

    @PutMapping("/columns/tasks/{id}")
    public ResponseEntity<Task> editTask(@RequestBody TaskDto taskDto, @PathVariable int id) {
        return new ResponseEntity<>(taskService.editTask(id, taskDto), HttpStatus.OK);
    }

    @PutMapping("/columns/{columnId}/tasks/{taskId}/order")
    public ResponseEntity<Task> changeTasksOrder(@RequestBody int newPosition, @PathVariable int columnId,
                                                 @PathVariable int taskId) {
        return new ResponseEntity<>(taskService.changeTasksOrder(columnId, taskId, newPosition), HttpStatus.OK);
    }

    @PutMapping("/columns/{columnId}/tasks/{taskId}/move")
    public ResponseEntity<Task> moveTaskToAnotherColumn(@RequestBody int newColumnId, @PathVariable int columnId,
                                                        @PathVariable int taskId) {
        return new ResponseEntity<>(taskService.moveTaskToAnotherColumn(columnId, taskId, newColumnId), HttpStatus.OK);
    }

    @DeleteMapping("/columns/{columnId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable int columnId, @PathVariable int taskId) {
        taskService.deleteTask(columnId, taskId);
    }
}
