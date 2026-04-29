package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureSubmitReviewResult(UUID lectureId, LectureStatus status) {

  public static LectureSubmitReviewResult from(Lecture lecture) {
    return new LectureSubmitReviewResult(lecture.getId(), lecture.getStatus());
  }
}
