package com.example.todolist.dto;

import com.example.todolist.entity.Comment;
import com.example.todolist.entity.Todo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TodoResponseDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> commentList;

    public TodoResponseDto(Todo todo){
        this.id=todo.getId();
        this.title = todo.getTitle();
        this.content = todo.getContent();
        this.username = todo.getUsername();
    }
    public TodoResponseDto(Todo todo, Comment comment){
        this.id=todo.getId();
        this.content=todo.getContent();
        this.title=todo.getTitle();
        this.username=todo.getUsername();
        this.createdAt=todo.getCreatedAt();
        this.modifiedAt=todo.getModifiedAt();

    }

}
