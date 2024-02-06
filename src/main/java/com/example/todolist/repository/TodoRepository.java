package com.example.todolist.repository;

import com.example.todolist.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository <Todo,Long> {
    List<Todo> findAllByOrderByModifiedAtDesc();
    Optional<Todo> findByIdAndUserId(Long id,Long userId);
}
