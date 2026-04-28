package com.goggles.lecture_service.domain.enrollment.exception;

import com.goggles.common.exception.NotFoundException;
import java.util.UUID;

public class EnrollmentNotFoundException extends NotFoundException {
  public EnrollmentNotFoundException(UUID id) {
    super(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND.getMessage() + " id=" + id);
  }
}
