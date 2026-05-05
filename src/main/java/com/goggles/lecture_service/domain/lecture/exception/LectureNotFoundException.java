package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.NotFoundException;
import java.util.UUID;

public class LectureNotFoundException extends NotFoundException {
  public LectureNotFoundException(UUID id) {
    super(LectureErrorCode.LECTURE_NOT_FOUND.getMessage() + " id=" + id);
  }
}
