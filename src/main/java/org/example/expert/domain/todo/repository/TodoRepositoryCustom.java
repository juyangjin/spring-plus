package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.expert.domain.todo.dto.request.SearchTodoRequestDto;
import org.example.expert.domain.todo.dto.response.SearchTodoResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TodoRepositoryCustom {

    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);


    @Query("SELECT t FROM Todo t WHERE " +
        "(:weather IS NULL OR t.weather = :weather) " +
        "AND (:startDate IS NULL OR t.modifiedAt >= :startDate) " +
        "AND (:endDate IS NULL OR t.modifiedAt <= :endDate) " +
        "ORDER BY t.modifiedAt DESC")
    Page<Todo> searchByWeatherByModifiedAt(String weather, LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable);

    Page<SearchTodoResponseDto.search> search(SearchTodoRequestDto requestDto, Pageable pageable);


}
