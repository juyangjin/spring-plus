package org.example.expert.domain.todo.dto.request;


import java.time.LocalDateTime;

public record SearchTodoRequestDto (String title, Long managerCount, Long commentCount, LocalDateTime start, LocalDateTime end){}

