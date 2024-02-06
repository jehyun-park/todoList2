package com.example.todolist.dto;

import lombok.Getter;

@Getter
public class CommentDeleteDto {
    private String response;

    public CommentDeleteDto(String response) {
        this.response = response;
    }
}