package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidRejectionReasonException extends BadRequestException {
  public InvalidRejectionReasonException() {
    super(LectureErrorCode.LECTURE_REJECTION_REASON_REQUIRED.getMessage());
  }
}
