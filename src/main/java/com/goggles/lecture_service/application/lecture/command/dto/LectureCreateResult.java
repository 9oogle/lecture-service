package com.goggles.lecture_service.application.lecture.command.dto;

import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import java.util.UUID;

public record LectureCreateResult(UUID id, LectureStatus status) {

  public static LectureCreateResult from(Lecture lecture) {
    return new LectureCreateResult(lecture.getId(), lecture.getStatus());
  }
}
