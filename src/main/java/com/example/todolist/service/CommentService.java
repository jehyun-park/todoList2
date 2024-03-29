package com.example.todolist.service;

import com.example.todolist.dto.*;
import com.example.todolist.entity.Comment;
import com.example.todolist.entity.Todo;
import com.example.todolist.entity.User;
import com.example.todolist.jwt.JwtUtil;
import com.example.todolist.repository.CommentRepository;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final JwtUtil jwtUtil;

    // 토큰 있는 경우에만 댓글 작성
    @Transactional
    public CommentResponseDto createComment(CommentRequestDto requestDto, HttpServletRequest request, Long postId) {
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
                throw new IllegalArgumentException("유효하지 않은 토");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("로그인 후 작성하세요.")
            );


            Todo todo = todoRepository.findById(postId).orElseThrow(
                    () -> new IllegalArgumentException("해당 아이디의 포스트가 없습니다.")
            );

            Comment comment = new Comment(requestDto, user.getUsername());
            comment.setTodosAndUsers(todo, user);
            commentRepository.save(comment);
            return new CommentResponseDto(comment);
        }else {
            return null;
        }
    }

    // 2. 토큰 있는 경우에만 댓글 수정
    @Transactional
    public CommentResponseDto update(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
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
                throw new IllegalArgumentException("토큰이 유효하지 않습니다");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 -> 로그인 안했으면 로그인 하라고 메시지
            Comment comment = commentRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("로그인 해주세요")
            );
            comment.update(requestDto);
            return new CommentResponseDto(comment);
        }else {
            return null;
        }
    }

    // 3. 토큰 있는 경우에만 댓글 삭제
    @Transactional
    public CommentDeleteDto delete(Long id, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            // 토큰 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            }else {
                throw new IllegalArgumentException("토큰이 유효하지 않습니다");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회 -> 로그인 안했으면 로그인 하라고 메시지
            Comment comment = commentRepository.findById(id).orElseThrow(
                    () -> new IllegalArgumentException("로그인 해주세요")
            );
            commentRepository.deleteById(id);
            String response = "삭제완료!";
            return new CommentDeleteDto(response);
        }
        return null;
    }
}
