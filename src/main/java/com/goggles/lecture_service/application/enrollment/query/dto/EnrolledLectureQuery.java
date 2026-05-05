package com.goggles.lecture_service.application.enrollment.query.dto;

import static com.goggles.lecture_service.domain.enrollment.exception.EnrollmentErrorCode.*;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.lecture_service.domain.enrollment.enums.EnrolledLectureSort;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentStatusException;
import java.util.UUID;

public record EnrolledLectureQuery(
    UUID studentId,
    String keyword,
    EnrollmentStatus status,
    EnrolledLectureSort sort,
    CommonPageRequest pageRequest) {

  public EnrolledLectureQuery {
    if (studentId == null) {
      throw new InvalidEnrollmentFieldException(ENROLLMENT_USER_ID_REQUIRED);
    }

    if (pageRequest == null) {
      throw new InvalidEnrollmentFieldException(ENROLLMENT_PAGE_REQUEST_REQUIRED);
    }
  }

  public static EnrolledLectureQuery of(
      UUID studentId, String keyword, String status, String sort, CommonPageRequest pageRequest) {
    return new EnrolledLectureQuery(
        studentId, keyword, parseStatus(status), EnrolledLectureSort.from(sort), pageRequest);
  }

  private static EnrollmentStatus parseStatus(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      EnrollmentStatus status = EnrollmentStatus.valueOf(value.toUpperCase());
      validateSearchableStatus(status);
      return status;
    } catch (IllegalArgumentException e) {
      throw new InvalidEnrollmentFieldException(ENROLLMENT_STATUS_INVALID);
    }
  }

  private static void validateSearchableStatus(EnrollmentStatus status) {
    if (status == EnrollmentStatus.RESERVE || status == EnrollmentStatus.CANCELED) {
      throw new InvalidEnrollmentStatusException(ENROLLMENT_STATUS_NOT_SEARCHABLE);
    }
  }
}
