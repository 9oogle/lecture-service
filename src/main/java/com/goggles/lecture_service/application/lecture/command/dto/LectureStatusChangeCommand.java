package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import java.util.UUID;

public record LectureStatusChangeCommand(
    UUID lectureId, UUID actorId, String actorRole, LectureStatus status, String rejectionReason) {
  public LectureStatusChangeCommand {
    if (lectureId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_ID_REQUIRED);
    }
    if (actorId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ID_REQUIRED);
    }
    if (actorRole == null || actorRole.isBlank()) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ROLE_REQUIRED);
    }
    if (status == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_STATUS_REQUIRED);
    }
    if (status == LectureStatus.DRAFT && (rejectionReason == null || rejectionReason.isBlank())) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_REJECTION_REASON_REQUIRED);
    }
  }
}
