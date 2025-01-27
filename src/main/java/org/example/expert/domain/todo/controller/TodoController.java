package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.CustomUserDetails;
import org.example.expert.domain.todo.dto.request.SearchTodoRequestDto;
import org.example.expert.domain.todo.dto.response.SearchTodoResponseDto;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final TodoRepository todoRepository;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(userDetails, todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponse>> getTodos(
        @RequestParam(required = false) String weather,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        LocalDateTime startDateTime = startDate != null ? LocalDateTime.parse(startDate + "T00:00:00") : null;
        LocalDateTime endDateTime = endDate != null ? LocalDateTime.parse(endDate + "T23:59:59") : null;

        return ResponseEntity.ok(todoService.getTodos(weather, startDateTime, endDateTime ,page, size));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }


    @GetMapping("/todos/search")
    public ResponseEntity<Page<SearchTodoResponseDto.search>> searchTodos(
        @ModelAttribute SearchTodoRequestDto requestDto,
        Pageable pageable
    ) {
        return ResponseEntity.ok(todoRepository.search(requestDto, pageable));

    }



}
