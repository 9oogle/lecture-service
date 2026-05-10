package com.goggles.lecture_service.infrastructure.enrollment.repository;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
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
      "select e.lectureSnapshot.lectureId from Enrollment e "
          + "where e.studentId = :studentId "
          + "and e.lectureSnapshot.lectureId in :lectureIds "
          + "and e.status in :statuses")
  List<UUID> findLectureIdsByStudentAndLectureIdInAndStatusIn(
      @Param("studentId") UUID studentId,
      @Param("lectureIds") Collection<UUID> lectureIds,
      @Param("statuses") Collection<EnrollmentStatus> statuses);

  List<Enrollment> findAllByOrderId(UUID orderId);

  List<Enrollment> findAllByIdIn(List<UUID> ids);

  @Query(
      "select e.id from Enrollment e "
          + "where e.status = :status "
          + "and e.expiresAt < :now "
          + "order by e.expiresAt asc")
  List<UUID> findExpirationTargetIds(
      @Param("status") EnrollmentStatus status, @Param("now") LocalDateTime now, Pageable pageable);
}
