package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureHideResult(UUID lectureId, LectureStatus status) {
  public static LectureHideResult from(Lecture lecture) {
    return new LectureHideResult(lecture.getId(), lecture.getStatus());
  }
}
