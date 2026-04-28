package com.goggles.lecture_service.domain.enrollment.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidEnrollmentStatusException extends BadRequestException {
  public InvalidEnrollmentStatusException(EnrollmentErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
