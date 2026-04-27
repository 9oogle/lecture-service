package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateResult;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureCreateResponse(UUID id, LectureStatus status) {
  public static LectureCreateResponse from(LectureCreateResult result) {
    return new LectureCreateResponse(result.id(), result.status());
  }
}
