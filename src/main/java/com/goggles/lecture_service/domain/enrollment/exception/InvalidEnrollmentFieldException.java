package com.goggles.lecture_service.domain.enrollment.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidEnrollmentFieldException extends BadRequestException {
  public InvalidEnrollmentFieldException(EnrollmentErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
