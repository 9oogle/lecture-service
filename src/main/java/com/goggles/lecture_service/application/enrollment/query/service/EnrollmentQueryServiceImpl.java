package com.goggles.lecture_service.application.enrollment.query.service;

import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureSummary;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentQueryServiceImpl implements EnrollmentQueryService {

  private final EnrollmentRepository enrollmentRepository;

  @Override
  public List<EnrolledLectureSummary> getEnrolledLectures(UUID studentId) {
    return enrollmentRepository.findActiveByStudentId(studentId).stream()
        .map(EnrolledLectureSummary::from)
        .toList();
  }
}
