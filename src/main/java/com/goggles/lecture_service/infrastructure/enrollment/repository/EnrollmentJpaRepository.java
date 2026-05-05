package com.goggles.lecture_service.infrastructure.enrollment.repository;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, UUID> {

  @Query(
      "select e from Enrollment e "
          + "where e.orderId = :orderId "
          + "and e.lectureSnapshot.lectureId = :lectureId")
  Optional<Enrollment> findByOrderIdAndLectureId(
      @Param("orderId") UUID orderId, @Param("lectureId") UUID lectureId);

  @Query(
      "select count(e) > 0 from Enrollment e "
          + "where e.studentId = :studentId "
          + "and e.lectureSnapshot.lectureId = :lectureId "
          + "and e.status in :statuses")
  boolean existsByStudentAndLectureAndStatusIn(
      @Param("studentId") UUID studentId,
      @Param("lectureId") UUID lectureId,
      @Param("statuses") Collection<EnrollmentStatus> statuses);

  List<Enrollment> findAllByOrderId(UUID orderId);

  List<Enrollment> findAllByIdIn(List<UUID> ids);
}
