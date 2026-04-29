package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import java.util.UUID;

public record LectureUpdateCommand(
    UUID lectureId,
    UUID actorId,
    String actorRole,
    String category,
    String title,
    String subtitle,
    String description,
    DurationPolicy durationPolicy,
    Long price) {

  public LectureUpdateCommand {
    if (lectureId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_ID_REQUIRED);
    }
    if (actorId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ID_REQUIRED);
    }
    if (actorRole == null || actorRole.isBlank()) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ROLE_REQUIRED);
    }
    if (price == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.PRICE_REQUIRED);
    }
  }
}
