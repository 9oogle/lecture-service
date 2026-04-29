package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import java.util.UUID;

public record LectureDeleteCommand(UUID lectureId, UUID actorId, String actorRole) {

  public LectureDeleteCommand {
    if (lectureId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_ID_REQUIRED);
    }
    if (actorId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ID_REQUIRED);
    }
    if (actorRole == null || actorRole.isBlank()) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ROLE_REQUIRED);
    }
  }
}
