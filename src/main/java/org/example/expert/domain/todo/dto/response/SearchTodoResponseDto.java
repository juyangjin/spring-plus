package org.example.expert.domain.todo.dto.response;



import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;

public class SearchTodoResponseDto {

    public record search(String title, LocalDateTime createdAt , long managerCount, long commentCount){
        public search (Todo todo, long managerCount, long commentCount) {
            this(
                todo.getTitle(),
                todo.getCreatedAt(),
                (int) managerCount,
                (int) commentCount
            );
        }

    }
}
