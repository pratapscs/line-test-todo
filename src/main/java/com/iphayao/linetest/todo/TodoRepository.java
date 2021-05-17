package com.iphayao.linetest.todo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findByUserId(String userId);
    List<Todo> findByUserIdAndImportanceTrueOrderByDateTime(String userId);
    List<Todo> findByUserIdAndImportanceFalseOrderByDateTime(String userId);
    List<Todo> findByUserIdAndTypeAndActionAndDateTime(String userId, TodoType type, String action, LocalDateTime dateTime);
    Todo findByIdAndUserId(int id, String userId);

    List<Todo> findByDoneTrueAndDateTimeBefore(LocalDateTime dateTime);
}
