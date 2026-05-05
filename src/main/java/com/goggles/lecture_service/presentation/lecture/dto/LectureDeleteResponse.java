package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureDeleteResult;
import java.util.UUID;

public record LectureDeleteResponse(UUID lectureId) {

  public static LectureDeleteResponse from(LectureDeleteResult result) {
    return new LectureDeleteResponse(result.lectureId());
  }
}
