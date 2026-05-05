package com.goggles.lecture_service.domain.enrollment.exception;

import com.goggles.common.exception.BadRequestException;

public class EnrollmentNotOwnedException extends BadRequestException {

  public EnrollmentNotOwnedException(EnrollmentErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
