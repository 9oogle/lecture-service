package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureResult;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import java.time.LocalDateTime;
import java.util.UUID;

public record EnrolledLectureResponse(
    UUID enrollmentId,
    UUID lectureId,
    String lectureTitle,
    UUID instructorId,
    String instructorName,
    EnrollmentStatus status,
    DurationPolicy durationPolicy,
    LocalDateTime activatedAt,
    LocalDateTime expiresAt,
    LocalDateTime lastAccessedAt) {

  public static EnrolledLectureResponse from(EnrolledLectureResult result) {
    return new EnrolledLectureResponse(
        result.enrollmentId(),
        result.lectureId(),
        result.lectureTitle(),
        result.instructorId(),
        result.instructorName(),
        result.status(),
        result.durationPolicy(),
        result.activatedAt(),
        result.expiresAt(),
        result.lastAccessedAt());
  }
}
