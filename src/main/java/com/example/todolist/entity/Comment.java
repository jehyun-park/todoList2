package com.example.todolist.entity;

import com.example.todolist.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Todo todos;

    public Comment(CommentRequestDto requestDto, String username){
        this.content=requestDto.getContent();
        this.username = username;
    }

    public void update(CommentRequestDto requestDto){
        this.content = requestDto.getContent();
        this.username = this.user.getUsername();
    }

    public void setTodosAndUsers(Todo todos, User user){
        this.todos=todos;
        this.user=user;
        todos.getCommentList().add(this);
    }
}
