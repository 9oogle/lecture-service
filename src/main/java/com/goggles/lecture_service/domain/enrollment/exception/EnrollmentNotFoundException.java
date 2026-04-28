package com.goggles.lecture_service.domain.enrollment.exception;

import com.goggles.common.exception.BadRequestException;

public class EnrollmentNotFoundException extends BadRequestException {
  public EnrollmentNotFoundException(EnrollmentErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
