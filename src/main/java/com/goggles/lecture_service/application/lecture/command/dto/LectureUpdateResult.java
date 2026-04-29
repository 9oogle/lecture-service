package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureUpdateResult(UUID lectureId, LectureStatus status) {

  public static LectureUpdateResult from(Lecture lecture) {
    return new LectureUpdateResult(lecture.getId(), lecture.getStatus());
  }
}
