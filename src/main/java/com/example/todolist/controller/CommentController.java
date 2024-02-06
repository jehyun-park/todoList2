package com.example.todolist.controller;

import com.example.todolist.dto.*;
import com.example.todolist.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/")
public class CommentController {
    private final CommentService commentService;

    // 1. 댓글 생성

    /**
     * 지금은 {id} 값 -> 내가 댓글을 쓰고 싶은 post id 잖아?
     *
     * 1. commentService.createComment(post_id정보도 파라미터로 넘겨줘);
     *
     * @param requestDto
     * @param request
     * @return
     */
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