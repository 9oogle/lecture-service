package com.goggles.lecture_service.infrastructure.enrollment.repository;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

  private final EnrollmentJpaRepository jpaRepository;

  @Override
  public Enrollment save(Enrollment enrollment) {
    return jpaRepository.save(enrollment);
  }

  @Override
  public Optional<Enrollment> findById(UUID id) {
    return jpaRepository.findById(id);
  }

  @Override
  public Optional<Enrollment> findByOrderIdAndLectureId(UUID orderId, UUID lectureId) {
    return jpaRepository.findByOrderIdAndLectureId(orderId, lectureId);
  }

  @Override
  public boolean existsActiveByStudentAndLecture(UUID studentId, UUID lectureId) {
    return jpaRepository.existsActiveByStudentAndLecture(studentId, lectureId);
  }

  @Override
  public List<Enrollment> findAllByOrderId(UUID orderId) {
    return jpaRepository.findAllByOrderId(orderId);
  }

  @Override
  public List<Enrollment> findActiveByStudentId(UUID studentId) {
    return jpaRepository.findActiveByStudentId(studentId);
  }
}
