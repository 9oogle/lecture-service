package com.goggles.lecture_service.domain.lecture.exception;

import com.goggles.common.exception.ConflictException;

public class DuplicateSortOrderException extends ConflictException {
  public DuplicateSortOrderException(int sortOrder) {
    super(LectureErrorCode.CHAPTER_DUPLICATE_SORT_ORDER.getMessage() + " sortOrder=" + sortOrder);
  }
}
