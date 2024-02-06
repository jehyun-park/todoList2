package com.example.todolist.service;


import com.example.todolist.dto.CommentResponseDto;
import com.example.todolist.dto.DeleteResponseDto;
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
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    //토큰이 있는 경우에만 게시글 작성
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto requestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if(token != null){
            if(jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
            }else{
                throw new IllegalArgumentException("유효하지 않은 토큰");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("로그인 후 작성하세요.")
            );
            Todo todo = new Todo(requestDto, user.getUsername());
            todo.setUser(user);
            todoRepository.save(todo);
            return new TodoResponseDto(todo);
        }else{
            return null;
        }
    }


    @Transactional
    public List<TodoResponseDto> getTodoList() {
        List<Todo> todos =  todoRepository.findAllByOrderByModifiedAtDesc();
        if(Collections.isEmpty(todos)) return null;
        List<TodoResponseDto> results = new ArrayList<>();

        for(Todo todo : todos) {
            List<Comment> comments = commentRepository.findAllByTodos(todo);
            TodoResponseDto todoResponseDto = new TodoResponseDto(todo);
            List<CommentResponseDto> commentResponseDto = new ArrayList<>();
            for (Comment comment : comments) {
                commentResponseDto.add(new CommentResponseDto(comment));
            }
            todoResponseDto.setCommentList(commentResponseDto);
            results.add(todoResponseDto);
        }
        return results;
    }

    //선택한 게시글 조회
    @Transactional
    //BoardResponseDto 반환 타입, getBoard 메소드 명, Long id: 담을 데이터
    public TodoResponseDto getTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("글이 존재하지 않습니다.")
        );
        TodoResponseDto todoDto = new TodoResponseDto();

        List<Comment> comments = commentRepository.findAllByTodos(todo);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for(Comment comment : comments){
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

    //선택한 게시글 삭제
    @Transactional
    public DeleteResponseDto delete(Long id, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token == null) {
            // 토큰이 없을 경우 예외를 던지거나 적절한 처리를 수행할 수 있습니다.
            throw new IllegalArgumentException("토큰이 존재하지 않습니다");
        }

        // 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다");
        }

        // 토큰에서 사용자 정보 가져오기
        claims = jwtUtil.getUserInfoFromToken(token);

        // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 -> 로그인 안했으면 로그인 하라는 메시지
        Todo todo = todoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 ID에 대한 할 일이 존재하지 않습니다")
        );

        // 사용자 정보와 할 일의 작성자 정보 일치 여부 확인
        String usernameFromToken = claims.getSubject();
        if (!todo.getUsername().equals(usernameFromToken)) {
            throw new IllegalArgumentException("해당 할 일에 대한 권한이 없습니다");
        }

        todoRepository.deleteById(id);
        String response = "삭제 완료!";
        return new DeleteResponseDto(response);
    }


    //할일 상태 변경
    @Transactional
    public TodoResponseDto todoStatus(Long id, boolean completed, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if(token == null){
            throw new IllegalArgumentException("로그인 후 변경하세요.");
        }

        if(!jwtUtil.validateToken(token)){
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
        claims = jwtUtil.getUserInfoFromToken(token);

        Todo todo = todoRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("해당 ID에 대한 할일이 존재하지 않습니다")
        );

        String usernameFromToken = claims.getSubject();
        if(!todo.getUsername().equals(usernameFromToken)){
            throw new IllegalArgumentException("해당 할일에 대한 권한X");
        }
        todo.setCompleted(completed);
        todoRepository.save(todo);

        return new TodoResponseDto(todo);
    }
}