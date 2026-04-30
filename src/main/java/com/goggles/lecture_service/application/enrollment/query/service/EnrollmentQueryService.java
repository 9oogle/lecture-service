package com.goggles.lecture_service.application.enrollment.query.service;

import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureQuery;
import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureResult;
import org.springframework.data.domain.Page;

public interface EnrollmentQueryService {

  Page<EnrolledLectureResult> getEnrolledLectures(EnrolledLectureQuery query);
}
