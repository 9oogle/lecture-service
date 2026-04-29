package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureStatusChangeResult(
    UUID lectureId, LectureStatus status, String rejectionReason) {

  public static LectureStatusChangeResult from(Lecture lecture) {
    return new LectureStatusChangeResult(
        lecture.getId(), lecture.getStatus(), lecture.getRejectionReason());
  }
}
