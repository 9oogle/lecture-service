package com.goggles.lecture_service.domain.enrollment.enums;

import static com.goggles.lecture_service.domain.enrollment.exception.EnrollmentErrorCode.*;

import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import java.util.Arrays;

public enum EnrolledLectureSort {
  RECENT_ACCESSED("recentAccessed"),
  EXPIRES_SOON("expiresSoon"),
  RECENT_ACTIVATED("recentActivated");

  private final String value;

  EnrolledLectureSort(String value) {
    this.value = value;
  }

  public static EnrolledLectureSort from(String value) {
    if (value == null || value.isBlank()) {
      return RECENT_ACCESSED;
    }

    return Arrays.stream(values())
        .filter(type -> type.value.equalsIgnoreCase(value))
        .findFirst()
        .orElseThrow(() -> new InvalidEnrollmentFieldException(ENROLLMENT_SORT_INVALID));
  }
}
