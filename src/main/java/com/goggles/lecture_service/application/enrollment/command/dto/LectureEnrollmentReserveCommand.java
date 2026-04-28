package com.goggles.lecture_service.application.enrollment.command.dto;

import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentErrorCode;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import java.util.List;
import java.util.UUID;

public record LectureEnrollmentReserveCommand(List<UUID> productIds, UUID userId, String userName) {

  public LectureEnrollmentReserveCommand {
    if (productIds == null) {
      throw new InvalidEnrollmentFieldException(
          EnrollmentErrorCode.ENROLLMENT_PRODUCT_IDS_REQUIRED);
    }
    if (productIds.isEmpty()) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_PRODUCT_IDS_EMPTY);
    }
    if (userId == null) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_USER_ID_REQUIRED);
    }
    if (userName == null || userName.isBlank()) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_USER_NAME_REQUIRED);
    }

    productIds = List.copyOf(productIds);
  }
}
