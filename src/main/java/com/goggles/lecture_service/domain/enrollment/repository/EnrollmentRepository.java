package com.goggles.lecture_service.domain.enrollment.repository;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface EnrollmentRepository {

  Enrollment save(Enrollment enrollment);

  Optional<Enrollment> findById(UUID id);

  List<Enrollment> findAllByIdIn(List<UUID> ids);

  Optional<Enrollment> findByOrderIdAndLectureId(UUID orderId, UUID lectureId);

  // 동일 학생 + 동일 강의에 RESERVE/ACTIVE 인 enrollment 존재 여부. 중복 수강 검증용
  boolean existsActiveByStudentAndLecture(UUID studentId, UUID lectureId);

  List<Enrollment> findAllByOrderId(UUID orderId);

  Page<Enrollment> findEnrolledLectures(EnrolledLecturePageQuery query);
}
