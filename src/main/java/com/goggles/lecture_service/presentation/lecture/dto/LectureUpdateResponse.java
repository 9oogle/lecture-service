package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateResult;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureUpdateResponse(UUID lectureId, LectureStatus status) {

  public static LectureUpdateResponse from(LectureUpdateResult result) {
    return new LectureUpdateResponse(result.lectureId(), result.status());
  }
}
