package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import java.util.UUID;

public record LectureRejectCommand(UUID lectureId, UUID actorId, String actorRole, String reason) {

  public LectureRejectCommand {
    if (lectureId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_ID_REQUIRED);
    }
    if (actorId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ID_REQUIRED);
    }
    if (actorRole == null || actorRole.isBlank()) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ROLE_REQUIRED);
    }
    if (reason == null || reason.isBlank()) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_REJECTION_REASON_REQUIRED);
    }
    if (reason.length() > 1000) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_REJECTION_REASON_TOO_LONG);
    }
  }
}
