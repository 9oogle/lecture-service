package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidCategoryException extends BadRequestException {
  public InvalidCategoryException() {
    super(LectureErrorCode.LECTURE_CATEGORY_REQUIRED.getMessage());
  }
}
