package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

public record ChapterReorderResult(UUID lectureId) {

  public static ChapterReorderResult from(UUID lectureId) {
    return new ChapterReorderResult(lectureId);
  }
}
