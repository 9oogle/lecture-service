package com.goggles.lecture_service.infrastructure.enrollment.repository;

import static com.goggles.lecture_service.domain.enrollment.QEnrollment.*;

import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.enums.EnrolledLectureSort;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.enrollment.repository.EnrolledLecturePageQuery;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EnrollmentQueryDslRepository {

  private static final Set<EnrollmentStatus> ENROLLED_HISTORY =
      EnumSet.of(EnrollmentStatus.ACTIVE, EnrollmentStatus.EXPIRED);

  private final JPAQueryFactory queryFactory;

  public <T> CommonPageResponse<T> findEnrolledLectures(
      EnrolledLecturePageQuery query, Function<Enrollment, T> mapper) {

    List<Enrollment> content =
        queryFactory
            .selectFrom(enrollment)
            .where(studentCondition(query), statusCondition(query), keywordCondition(query))
            .orderBy(orderSpecifiers(query.sort()))
            .offset((long) query.page() * query.size())
            .limit(query.size())
            .fetch();

    Long total =
        queryFactory
            .select(enrollment.count())
            .from(enrollment)
            .where(studentCondition(query), statusCondition(query), keywordCondition(query))
            .fetchOne();

    Pageable pageable = PageRequest.of(query.page(), query.size());
    Page<Enrollment> page = new PageImpl<>(content, pageable, total != null ? total : 0L);
    return CommonPageResponse.of(page, mapper);
  }

  private BooleanExpression studentCondition(EnrolledLecturePageQuery query) {
    return enrollment.studentId.eq(query.studentId());
  }

  private BooleanExpression statusCondition(EnrolledLecturePageQuery query) {
    if (query.status() != null) {
      return enrollment.status.eq(query.status());
    }

    return enrollment.status.in(ENROLLED_HISTORY);
  }

  private BooleanExpression keywordCondition(EnrolledLecturePageQuery query) {
    if (query.keyword() == null || query.keyword().isBlank()) {
      return null;
    }

    String keyword = query.keyword().trim();

    return enrollment
        .lectureSnapshot
        .lectureTitle
        .containsIgnoreCase(keyword)
        .or(enrollment.lectureSnapshot.instructorName.containsIgnoreCase(keyword));
  }

  private OrderSpecifier<?>[] orderSpecifiers(EnrolledLectureSort sort) {
    EnrolledLectureSort resolved = sort == null ? EnrolledLectureSort.RECENT_ACCESSED : sort;

    return switch (resolved) {
      case RECENT_ACCESSED ->
          new OrderSpecifier<?>[] {
            enrollment.lastAccessedAt.desc().nullsLast(),
            enrollment.activatedAt.desc().nullsLast(),
            enrollment.id.desc()
          };

      case EXPIRES_SOON ->
          new OrderSpecifier<?>[] {enrollment.expiresAt.asc().nullsLast(), enrollment.id.desc()};

      case RECENT_ACTIVATED ->
          new OrderSpecifier<?>[] {enrollment.activatedAt.desc().nullsLast(), enrollment.id.desc()};
    };
  }
}
