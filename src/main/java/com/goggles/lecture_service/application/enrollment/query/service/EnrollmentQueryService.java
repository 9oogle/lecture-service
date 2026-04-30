package com.goggles.lecture_service.application.enrollment.query.service;

import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureSummary;
import java.util.List;
import java.util.UUID;

public interface EnrollmentQueryService {

  List<EnrolledLectureSummary> getEnrolledLectures(UUID studentId);
}
