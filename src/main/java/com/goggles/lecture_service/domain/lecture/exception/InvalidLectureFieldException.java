package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidLectureFieldException extends BadRequestException {
  public InvalidLectureFieldException(LectureErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
