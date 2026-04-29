package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureSubmitReviewResult;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureSubmitReviewResponse(UUID lectureId, LectureStatus status) {

  public static LectureSubmitReviewResponse from(LectureSubmitReviewResult result) {
    return new LectureSubmitReviewResponse(result.lectureId(), result.status());
  }
}
