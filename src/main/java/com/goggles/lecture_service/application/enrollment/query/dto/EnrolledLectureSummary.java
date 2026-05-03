package com.goggles.lecture_service.application.enrollment.query.dto;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import java.time.LocalDateTime;
import java.util.UUID;

public record EnrolledLectureSummary(
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

  public static EnrolledLectureSummary from(Enrollment enrollment) {
    return new EnrolledLectureSummary(
        enrollment.getId(),
        enrollment.getLectureSnapshot().getLectureId(),
        enrollment.getLectureSnapshot().getLectureTitle(),
        enrollment.getLectureSnapshot().getInstructorId(),
        enrollment.getLectureSnapshot().getInstructorName(),
        enrollment.getStatus(),
        enrollment.getDurationPolicy(),
        enrollment.getActivatedAt(),
        enrollment.getExpiresAt(),
        enrollment.getLastAccessedAt());
  }
}
