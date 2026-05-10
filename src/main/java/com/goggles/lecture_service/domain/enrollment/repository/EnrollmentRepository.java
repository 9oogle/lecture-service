package com.goggles.lecture_service.domain.enrollment.repository;

import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.domain.enrollment.Enrollment;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public interface EnrollmentRepository {

  Enrollment save(Enrollment enrollment);

  Optional<Enrollment> findById(UUID id);

  List<Enrollment> findAllByIdIn(List<UUID> ids);

  Optional<Enrollment> findByOrderIdAndLectureId(UUID orderId, UUID lectureId);

  // 학생이 이미 RESERVE/ACTIVE 상태로 보유한 lectureId 집합 반환 / 중복 수강 검증용 (배치)
  Set<UUID> findActiveLectureIdsByStudentAndLectureIdIn(
      UUID studentId, Collection<UUID> lectureIds);

  List<Enrollment> findAllByOrderId(UUID orderId);

  <T> CommonPageResponse<T> findEnrolledLectures(
      EnrolledLecturePageQuery query, Function<Enrollment, T> mapper);

  List<UUID> findExpirationTargetIds(LocalDateTime now, int limit);

  void deleteAllByIdIn(List<UUID> ids);
}
