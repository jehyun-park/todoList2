package com.example.todolist.service;


import com.example.todolist.dto.CommentResponseDto;
import com.example.todolist.dto.TodoRequestDto;
import com.example.todolist.dto.TodoResponseDto;
import com.example.todolist.entity.Comment;
import com.example.todolist.entity.Todo;
import com.example.todolist.entity.User;
import com.example.todolist.jwt.JwtUtil;
import com.example.todolist.repository.CommentRepository;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    //토큰이 있는 경우에만 글 작성 가능
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto requestDto, HttpServletRequest request) {
        // 사용자의 정보 가져오기. request에서 Token가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        // 토큰이 있는 경우에만 글 작성 가능
        if (token != null) {
            // 토큰 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            }else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 -> 로그인 안했으면 로그인 하라고 메시지
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("로그인 해주세요")
            );
            Todo todo = new Todo(requestDto, user.getUsername());
            todo.setUser(user); // 추가함. 윗줄보다 이렇게 추가하는게 낫다.
            // user.add(post); // User에서 만든 add메서드를 이렇게 추가해도 된다!!
            todoRepository.save(todo);
            return new TodoResponseDto(todo);
        }else {
            return null;
        }
    }

    //게시글 목록조회
    @Transactional
    public List<TodoResponseDto> getTodoList() {
        List<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(); // List<Post> 꺼내오기
        if(Collections.isEmpty(todos)) return null;
        List<TodoResponseDto> results = new ArrayList<>(); // List<PostResponseDto> 빈통 만들기 (주연)

        for(Todo todo : todos) {
            List<Comment> comments = commentRepository.findAllByTodos(todo); // List<Comment> 꺼내오기 (코멘트 조연)
            TodoResponseDto todoResponseDto = new TodoResponseDto(todo); // PostResponseDto 빈통 만들기 (조연)
            List<CommentResponseDto> commentResponseDtos = new ArrayList<>(); // List<CommentResponseDto> 빈통 만들기(코멘트 주연)
            for (Comment comment : comments) {
                commentResponseDtos.add(new CommentResponseDto(comment)); // List<CommentResponseDto>에 CommnetResponseDto를 add하기
            }
            todoResponseDto.setCommentList(commentResponseDtos); // postResponseDto 에 CommentList 세팅하기
            results.add(todoResponseDto); // List<PostResponseDto> 에 postResponseDto를 add 하기
        }
        return results;
    }

    // 3. 선택한 게시글 조회 -> 예외처리("게시글이 존재하지 않습니다")
    @Transactional
    public TodoResponseDto getTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow( // Post 꺼내오기
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        TodoResponseDto todoDto = new TodoResponseDto(todo); // PostResponseDto 빈통 만들기 (주연)

        List<Comment> comments = commentRepository.findAllByTodos(todo);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDtos.add(new CommentResponseDto(comment));
        }
        todoDto.setCommentList(commentResponseDtos);
        return todoDto;
    }
    //4. 토큰이 있는 경우만 게시글 수정 -> ("아이디가 존재하지 않습니다")
    @Transactional
    public TodoResponseDto update(Long id, TodoRequestDto requestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            // 토큰 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기(claims)
                claims = jwtUtil.getUserInfoFromToken(token);
            }else {
                throw new IllegalArgumentException("토큰이 유효하지 않습니다");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 -> 로그인 안했으면 로그인 하라고 메시지
            Todo todo = todoRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("아이디가 존재하지 않습니다")
            );
            todo.update(requestDto);
            todoRepository.save(todo);
            return new TodoResponseDto(todo);
        }else {
            return null;
        }
    }

}
