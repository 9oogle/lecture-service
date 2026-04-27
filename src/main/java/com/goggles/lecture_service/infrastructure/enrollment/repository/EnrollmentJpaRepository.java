package com.goggles.lecture_service.infrastructure.enrollment.repository;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, UUID> {

  @Query(
      "select e from Enrollment e "
          + "where e.orderId = :orderId "
          + "and e.lectureSnapshot.lectureId = :lectureId")
  Optional<Enrollment> findByOrderIdAndLectureId(UUID orderId, UUID lectureId);

  @Query(
      "select count(e) > 0 from Enrollment e "
          + "where e.studentId = :studentId "
          + "and e.lectureSnapshot.lectureId = :lectureId "
          + "and e.status in (com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus.RESERVE, "
          + "                 com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus.ACTIVE)")
  boolean existsActiveByStudentAndLecture(UUID studentId, UUID lectureId);

  List<Enrollment> findAllByOrderId(UUID orderId);

  @Query(
      "select e from Enrollment e "
          + "where e.studentId = :studentId "
          + "and e.status = com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus.ACTIVE")
  List<Enrollment> findActiveByStudentId(UUID studentId);
}
