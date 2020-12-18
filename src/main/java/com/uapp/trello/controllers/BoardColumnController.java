package com.uapp.trello.controllers;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.service.BoardColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BoardColumnController {

    private final BoardColumnService boardColumnService;

    @Autowired
    public BoardColumnController(BoardColumnService boardColumnService) {
        this.boardColumnService = boardColumnService;
    }

    @PostMapping("/columns")
    public ResponseEntity<BoardColumn> createBoardColumn(@RequestBody String name) {
        return new ResponseEntity<>(boardColumnService.createColumn(name), HttpStatus.CREATED);
    }

    @PutMapping("/columns/{id}")
    public ResponseEntity<BoardColumn> editBoardColumn(@RequestBody String name, @PathVariable int id) {
        try {
            return new ResponseEntity<>(boardColumnService.editColumn(id, name), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/columns/{id}/move")
    public ResponseEntity<BoardColumn> changeBoardColumnsOrder(@RequestBody int newPosition, @PathVariable int id) {
        return new ResponseEntity<>(boardColumnService.changeColumnOrder(id, newPosition), HttpStatus.OK);
    }

    @DeleteMapping("/columns/{id}")
    public ResponseEntity<List<BoardColumn>> deleteBoardColumn(@PathVariable int id) {
        try {
            return new ResponseEntity<>(boardColumnService.deleteColumn(id), HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
