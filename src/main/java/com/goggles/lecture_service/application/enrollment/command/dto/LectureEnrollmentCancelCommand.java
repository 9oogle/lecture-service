package com.goggles.lecture_service.application.enrollment.command.dto;

import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentErrorCode;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public record LectureEnrollmentCancelCommand(List<UUID> enrollmentIds, UUID userId) {

  public LectureEnrollmentCancelCommand {
    if (enrollmentIds == null) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_IDS_REQUIRED);
    }
    if (enrollmentIds.isEmpty()) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_IDS_EMPTY);
    }
    if (enrollmentIds.stream().anyMatch(id -> id == null)) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_ID_REQUIRED);
    }
    if (new HashSet<>(enrollmentIds).size() != enrollmentIds.size()) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_IDS_DUPLICATED);
    }
    if (userId == null) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_USER_ID_REQUIRED);
    }

    enrollmentIds = List.copyOf(enrollmentIds);
  }
}
