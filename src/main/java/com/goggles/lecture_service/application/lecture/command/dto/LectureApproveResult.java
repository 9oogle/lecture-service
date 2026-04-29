package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureApproveResult(UUID lectureId, LectureStatus status) {
  public static LectureApproveResult from(Lecture lecture) {
    return new LectureApproveResult(lecture.getId(), lecture.getStatus());
  }
}
