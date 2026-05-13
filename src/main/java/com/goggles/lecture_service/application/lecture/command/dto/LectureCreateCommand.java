package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain._common.UserType;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import java.util.UUID;

public record LectureCreateCommand(
    UUID instructorId,
    String instructorName,
    UserType actorRole,
    String category,
    String title,
    String subtitle,
    String description,
    DurationPolicy durationPolicy,
    Long price) {
  public LectureCreateCommand {
    if (instructorId == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ID_REQUIRED);
    }
    if (actorRole == null) {
      throw new InvalidLectureFieldException(LectureErrorCode.USER_ROLE_REQUIRED);
    }
  }
}
