package com.example.todolist.controller;

import com.example.todolist.dto.*;
import com.example.todolist.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/")
public class CommentController {
    private final CommentService commentService;

    // 1. 댓글 생성
    @PostMapping("/api/comment/{id}")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto requestDto, HttpServletRequest request, @PathVariable long id) {
        return commentService.createComment(requestDto, request, id);
    }
    // 2. 댓글 수정
    @PutMapping("/api/comment/{id}")
    public CommentResponseDto update(@PathVariable Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest request) {
        return commentService.update(id, requestDto, request);
    }
    // 3. 댓글 삭제
    @DeleteMapping("/api/comment/{id}")
    public CommentDeleteDto delete(@PathVariable Long id, HttpServletRequest request) {
        return commentService.delete(id, request);
    }
}