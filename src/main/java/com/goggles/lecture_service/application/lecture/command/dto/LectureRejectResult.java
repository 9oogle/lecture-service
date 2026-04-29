package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureRejectResult(UUID lectureId, LectureStatus status, String rejectionReason) {
  public static LectureRejectResult from(Lecture lecture) {
    return new LectureRejectResult(
        lecture.getId(), lecture.getStatus(), lecture.getRejectionReason());
  }
}
