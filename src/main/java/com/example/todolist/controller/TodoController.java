package com.example.todolist.controller;


import com.example.todolist.dto.TodoRequestDto;
import com.example.todolist.dto.TodoResponseDto;
import com.example.todolist.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TodoController {
    private final TodoService todoService;

    // 1. 게시글 생성
    @PostMapping("/todo")
    public TodoResponseDto createTodo(@RequestBody TodoRequestDto requestDto, HttpServletRequest request) {
        return todoService.createTodo(requestDto, request);
    }
    // 2. 게시글 전체 목록 조회
    @GetMapping("/todo")
    public List<TodoResponseDto> getTodoList(){
        return todoService.getTodoList();
    }
    // 3. 선택한 게시글 조회
    @GetMapping("/todo/{id}")
    public TodoResponseDto getTodo(@PathVariable Long id) {
        return todoService.getTodo(id);
    }
    // 4. 선택한 게시글 수정
    @PutMapping("/todo/{id}")
    public TodoResponseDto update(@PathVariable Long id, @RequestBody TodoRequestDto requestDto, HttpServletRequest request) {
        return todoService.update(id, requestDto, request);
    }
    // 5. 선택한 게시글 삭제
//    @DeleteMapping("/todo/{id}")
//    public DeleteReponseDto delete(@PathVariable Long id, HttpServletRequest request) {
//        return todoService.delete(id, request);
//    }

}
