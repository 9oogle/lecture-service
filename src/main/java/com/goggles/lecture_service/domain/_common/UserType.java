package com.goggles.lecture_service.domain._common;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;

public enum UserType {
  STUDENT,
  INSTRUCTOR,
  MASTER;

  public static UserType from(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ROLE_REQUIRED);
    }
    try {
      return UserType.valueOf(value.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ROLE_INVALID);
    }
  }
}
