package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidLectureStatusException extends BadRequestException {
  public InvalidLectureStatusException(LectureErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
