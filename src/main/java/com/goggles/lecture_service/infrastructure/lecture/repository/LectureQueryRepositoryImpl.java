package com.goggles.lecture_service.infrastructure.lecture.repository;

import static com.goggles.lecture_service.domain.lecture.QLecture.*;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class LectureQueryRepositoryImpl implements LectureQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Lecture> findAllByCondition(
      LectureSearchCondition condition, CommonPageRequest pageRequest) {

    Pageable pageable = pageRequest.toPageable(Sort.by(Sort.Direction.DESC, "createdAt"));

    // 1. 조건에 맞는 content 조회 (페이징 + 정렬 적용)
    List<Lecture> content =
        queryFactory
            .selectFrom(lecture)
            .where(
                statusEq(condition.status()),
                keywordContains(condition.keyword()),
                categoryEq(condition.category()),
                priceGoe(condition.minPrice()),
                priceLoe(condition.maxPrice()),
                durationPolicyEq(condition.durationPolicy()))
            .orderBy(lecture.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    // 2. 전체 건수 조회 (같은 조건)
    Long total =
        queryFactory
            .select(lecture.count())
            .from(lecture)
            .where(
                statusEq(condition.status()),
                keywordContains(condition.keyword()),
                categoryEq(condition.category()),
                priceGoe(condition.minPrice()),
                priceLoe(condition.maxPrice()),
                durationPolicyEq(condition.durationPolicy()))
            .fetchOne();

    return new PageImpl<>(content, pageable, total != null ? total : 0L);
  }

  // ── BooleanExpression (null 반환 시 where에서 자동 제외) ──

  private BooleanExpression keywordContains(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return null;
    }
    return lecture
        .content
        .title
        .containsIgnoreCase(keyword)
        .or(lecture.instructor.instructorName.containsIgnoreCase(keyword));
  }

  private BooleanExpression categoryEq(String category) {
    return StringUtils.hasText(category) ? lecture.category.eq(category) : null;
  }

  private BooleanExpression priceGoe(Long minPrice) {
    return minPrice != null ? lecture.price.amount.goe(minPrice) : null;
  }

  private BooleanExpression priceLoe(Long maxPrice) {
    return maxPrice != null ? lecture.price.amount.loe(maxPrice) : null;
  }

  private BooleanExpression durationPolicyEq(DurationPolicy durationPolicy) {
    return durationPolicy != null ? lecture.durationPolicy.eq(durationPolicy) : null;
  }

  private BooleanExpression statusEq(LectureStatus status) {
    return status != null ? lecture.status.eq(status) : null;
  }
}
