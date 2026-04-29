package com.goggles.lecture_service.application.enrollment.command.dto;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.lecture.Lecture;
import java.util.UUID;

public record LectureEnrollmentReserveResult(
    UUID enrollmentId,
    UUID productId,
    String productName,
    Long productPrice,
    UUID instructorId,
    String instructorName) {

  public static LectureEnrollmentReserveResult of(Enrollment enrollment, Lecture lecture) {
    return new LectureEnrollmentReserveResult(
        enrollment.getId(),
        lecture.getId(),
        lecture.getContent().getTitle(),
        lecture.getPrice().getAmount(),
        lecture.getInstructor().getInstructorId(),
        lecture.getInstructor().getInstructorName());
  }
}
