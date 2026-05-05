package com.goggles.lecture_service.presentation.enrollment.internal.dto;

import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveResult;
import java.util.UUID;

public record LectureEnrollmentReserveResponse(
    UUID enrollmentId,
    UUID productId,
    String productName,
    Long productPrice,
    UUID instructorId,
    String instructorName) {

  public static LectureEnrollmentReserveResponse from(LectureEnrollmentReserveResult result) {
    return new LectureEnrollmentReserveResponse(
        result.enrollmentId(),
        result.productId(),
        result.productName(),
        result.productPrice(),
        result.instructorId(),
        result.instructorName());
  }
}
