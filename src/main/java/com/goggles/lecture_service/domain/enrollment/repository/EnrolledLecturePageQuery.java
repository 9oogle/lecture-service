package com.goggles.lecture_service.domain.enrollment.repository;

import com.goggles.lecture_service.domain.enrollment.enums.EnrolledLectureSort;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import java.util.UUID;

public record EnrolledLecturePageQuery(
    UUID studentId,
    String keyword,
    EnrollmentStatus status,
    EnrolledLectureSort sort,
    int page,
    int size) {}
