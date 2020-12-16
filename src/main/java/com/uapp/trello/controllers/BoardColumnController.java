package com.uapp.trello.controllers;

import com.uapp.trello.objects.BoardColumn;
import com.uapp.trello.objects.dto.BoardColumnDto;
import com.uapp.trello.service.BoardColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BoardColumnController {

    private final BoardColumnService boardColumnService;

    @Autowired
    public BoardColumnController(BoardColumnService boardColumnService) {
        this.boardColumnService = boardColumnService;
    }

    @PostMapping("/columns")
    public ResponseEntity<BoardColumn> createBoardColumn(@RequestBody BoardColumnDto boardColumnDto) {
        return new ResponseEntity<>(boardColumnService.createColumn(boardColumnDto), HttpStatus.CREATED);
    }

    @PutMapping("/columns/{id}")
    public ResponseEntity<BoardColumn> editBoardColumn(@RequestBody String name, @PathVariable int id) {
        return new ResponseEntity<>(boardColumnService.editColumn(id, name), HttpStatus.OK);
    }

    @PutMapping("/columns/{id}")
    public ResponseEntity<BoardColumn> editBoardColumn(@RequestBody int newPosition, @PathVariable int id) {
        return new ResponseEntity<>(boardColumnService.changeColumnOrder(id, newPosition), HttpStatus.OK);
    }

    @DeleteMapping("/columns/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editBoardColumn(@PathVariable int id) {
        boardColumnService.deleteColumn(id);
    }
}
