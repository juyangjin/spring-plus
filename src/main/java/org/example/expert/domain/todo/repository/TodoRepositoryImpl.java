package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    private EntityManager entityManager;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo qTodo = QTodo.todo;
        QUser qUser = QUser.user;

        return Optional.ofNullable(jpaQueryFactory.selectFrom(qTodo)
            .leftJoin(qTodo.user, qUser).fetchJoin()
            .where(QTodo.todo.id.eq(todoId))
            .fetchOne());
    }

    @Override
    public Page<Todo> searchByWeatherByModifiedAt(String weather, LocalDateTime startDate,
        LocalDateTime endDate, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Todo> cq = cb.createQuery(Todo.class);
        Root<Todo> todos = cq.from(Todo.class);

        List<Predicate> predicate = new ArrayList<>();

        if(weather != null && !weather.isBlank()){
            predicate.add(cb.equal(todos.get("weather"), weather));
        }

        if(startDate != null){
            predicate.add(cb.greaterThanOrEqualTo(todos.get("modifiedAt"), startDate));
        }

        if(endDate != null){
            predicate.add(cb.lessThanOrEqualTo(todos.get("modifiedAt"), endDate));
        }

        cq.select(todos).where(cb.and(predicate.toArray(new Predicate[0])));

        // 페이징 적용
        TypedQuery<Todo> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset()); // 시작 위치
        query.setMaxResults(pageable.getPageSize());    // 한 페이지에 표시할 데이터 수

        // 페이징된 결과 리스트
        List<Todo> todoList = query.getResultList();

        // 총 데이터 개수 구하기 (total count)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Todo> countRoot = countQuery.from(Todo.class);
        countQuery.select(cb.count(countRoot)).where(cb.and(predicate.toArray(new Predicate[0])));
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);
        Long totalCount = countTypedQuery.getSingleResult();

        // Page 객체로 반환
        return new PageImpl<>(todoList, pageable, totalCount);
    }
}
