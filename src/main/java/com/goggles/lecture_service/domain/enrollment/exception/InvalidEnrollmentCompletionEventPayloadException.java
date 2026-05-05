package com.goggles.lecture_service.domain.enrollment.exception;

import static com.goggles.lecture_service.domain.enrollment.exception.EnrollmentErrorCode.*;

import com.goggles.common.exception.BadRequestException;

public class InvalidEnrollmentCompletionEventPayloadException extends BadRequestException {

  public InvalidEnrollmentCompletionEventPayloadException() {
    super(ENROLLMENT_COMPLETION_EVENT_PAYLOAD_INVALID.getMessage());
  }
}
