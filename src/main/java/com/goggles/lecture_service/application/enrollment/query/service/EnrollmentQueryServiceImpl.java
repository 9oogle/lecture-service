package com.goggles.lecture_service.application.enrollment.query.service;

import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureQuery;
import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureResult;
import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.repository.EnrolledLecturePageQuery;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentQueryServiceImpl implements EnrollmentQueryService {

  private final EnrollmentRepository enrollmentRepository;

  @Override
  public Page<EnrolledLectureResult> getEnrolledLectures(EnrolledLectureQuery query) {
    EnrolledLecturePageQuery pageQuery =
        new EnrolledLecturePageQuery(
            query.studentId(),
            query.keyword(),
            query.status(),
            query.sort(),
            query.pageRequest().getPage(),
            query.pageRequest().getSize());

    Page<Enrollment> enrollments = enrollmentRepository.findEnrolledLectures(pageQuery);

    return enrollments.map(EnrolledLectureResult::from);
  }
}
