package com.goggles.lecture_service.application.enrollment.query.service;

import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureQuery;
import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureResult;

public interface EnrollmentQueryService {

  CommonPageResponse<EnrolledLectureResult> getEnrolledLectures(EnrolledLectureQuery query);
}
