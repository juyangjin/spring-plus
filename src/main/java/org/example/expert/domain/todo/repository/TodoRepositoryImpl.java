package org.example.expert.domain.todo.repository;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.SearchTodoRequestDto;
import org.example.expert.domain.todo.dto.response.SearchTodoResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(todo)
            .leftJoin(todo.user, user)
            .fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne());
    }


    @Override
    public Page<Todo> searchByWeatherByModifiedAt(String keyword, LocalDateTime startDate,
        LocalDateTime endDate, Pageable pageable) {

        // 동적 조건 생성
        BooleanBuilder conditions = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            conditions.and(todo.title.containsIgnoreCase(keyword));
        }

        if (startDate != null && endDate != null) {
            conditions.and(todo.createdAt.between(startDate, endDate));
        }

        if (startDate != null) {
            conditions.and(todo.createdAt.goe(startDate));
        }
        if (endDate != null) {
            conditions.and(todo.createdAt.loe(endDate));
        }

        /*
        담당자 수 찾기
        댓글 수 찾기
        projections.construct 사용하기
         */

         List<Todo> todos = jpaQueryFactory.selectFrom(todo)
            .where(conditions)
            .offset(pageable.getOffset()) //이 부분 공부 더 하기
            .limit(pageable.getPageSize())
            .fetch();

         Long totalCount = Optional.ofNullable(jpaQueryFactory.select(Wildcard.count)
             .from(todo)
             .where(conditions)
             .fetchOne()).orElse(0L);


         return new PageImpl<>(todos, pageable, totalCount);
    }



    /*
    QueryDSL로 Projection 적용해서 검색하기 기능 중에 조건절 마음에 안드는 부분 개선이 필요해 보인다. 정렬이 제대로 안 될 거 같다.
     */
    @Override
    public Page<SearchTodoResponseDto.search> search(SearchTodoRequestDto requestDto, Pageable pageable) {

        List<SearchTodoResponseDto.search> todos = jpaQueryFactory.select(
                Projections.constructor(
                    SearchTodoResponseDto.search.class,
                    todo.title,
                    todo.createdAt,
                    select(Wildcard.count).from(manager).where(todo.id.eq(manager.todo.id)),
                    select(Wildcard.count).from(comment).where(todo.id.eq(comment.todo.id))
                )
            )
            .from(todo)
            .where(
                eqTitle(requestDto.title())
                //todo.createdAt.between(requestDto.start(),requestDto.end())) //이 부분 이렇게 하면 안될 거 같다.
            )
            .offset(pageable.getOffset()) //이 부분 공부 더 하기
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = Optional.ofNullable(jpaQueryFactory.select(Wildcard.count)
            .from(todo)
            .where(eqTitle(requestDto.title()))
            .fetchOne()).orElse(0L);


        return new PageImpl<>(todos, pageable, totalCount);
    }

    private BooleanExpression eqTitle(String title){
        if(title == null){
            return null;
        }
        return todo.title.eq(title);
    }

//    private OrderSpecifier<?>[]getSortOrders(Pageable pageable) {
//        return new OrderSpecifier[0];
//    }

}
