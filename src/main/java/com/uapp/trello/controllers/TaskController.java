package com.uapp.trello.controllers;

import com.uapp.trello.objects.Task;
import com.uapp.trello.objects.dto.TaskDto;
import com.uapp.trello.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/columns/{columnID}/tasks")
    public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto, @PathVariable int columnID) {
        return new ResponseEntity<>(taskService.createTask(columnID, taskDto), HttpStatus.CREATED);
    }

    @PutMapping("/columns/**/tasks/{taskId}")
    public ResponseEntity<Task> editTask(@RequestBody TaskDto taskDto, @PathVariable int taskId) {
        try {
            return new ResponseEntity<>(taskService.editTask(taskId, taskDto), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/columns/{columnId}/tasks/{taskId}/order")
    public ResponseEntity<Task> changeTasksOrder(@RequestBody int newPosition, @PathVariable int columnId,
                                                 @PathVariable int taskId) {
        try {
            return new ResponseEntity<>(taskService.changeTasksOrder(columnId, taskId, newPosition), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/columns/{columnId}/tasks/{taskId}/move")
    public ResponseEntity<Task> moveTaskToAnotherColumn(@RequestBody int newColumnId, @PathVariable int columnId,
                                                        @PathVariable int taskId) {
        try {
            return new ResponseEntity<>(taskService.moveTaskToAnotherColumn(columnId, taskId, newColumnId), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/columns/{columnId}/tasks/{taskId}")
    public ResponseEntity<List<Task>> deleteTask(@PathVariable int columnId, @PathVariable int taskId) {
        try {
            return new ResponseEntity<>(taskService.deleteTask(columnId, taskId), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
