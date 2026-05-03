package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureStatusChangeResult;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureStatusChangeResponse(
    UUID lectureId, LectureStatus status, String rejectionReason) {

  public static LectureStatusChangeResponse from(LectureStatusChangeResult result) {
    return new LectureStatusChangeResponse(
        result.lectureId(), result.status(), result.rejectionReason());
  }
}
