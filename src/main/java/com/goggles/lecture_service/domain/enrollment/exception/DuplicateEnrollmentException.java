package com.goggles.lecture_service.domain.enrollment.exception;

import com.goggles.common.exception.ConflictException;
import java.util.UUID;

public class DuplicateEnrollmentException extends ConflictException {
  public DuplicateEnrollmentException(UUID lectureId, UUID studentId) {
    super(
        EnrollmentErrorCode.ENROLLMENT_DUPLICATE.getMessage()
            + " lectureId="
            + lectureId
            + ", studentId="
            + studentId);
  }
}
