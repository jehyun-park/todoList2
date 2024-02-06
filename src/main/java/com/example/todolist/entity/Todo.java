package com.example.todolist.entity;

import com.example.todolist.dto.TodoRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@NoArgsConstructor

public class Todo extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean completed;

    //cascade = CascadeType.REMOVE : 글 삭제시 해당 글의 댓글까지 함께 삭제
    @OneToMany(mappedBy = "todos", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //게시글 작성
    public Todo(TodoRequestDto requestDto, String username) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.username = username;
        this.completed = false;
    }

    //선택한 게시글 수정(변경)
    public void update(TodoRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }

    public void setUser(User user){
        this.user = user;
        user.getTodos().add(this);
    }

}
