package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface TodoRepositoryCustom {

    @Query("SELECT t FROM Todo t WHERE " +
        "(:weather IS NULL OR t.weather = :weather) " +
        "AND (:startDate IS NULL OR t.modifiedAt >= :startDate) " +
        "AND (:endDate IS NULL OR t.modifiedAt <= :endDate) " +
        "ORDER BY t.modifiedAt DESC")
    Page<Todo> searchByWeatherByModifiedAt(String weather, LocalDateTime startDate, LocalDateTime endDate,
        Pageable pageable);
}
