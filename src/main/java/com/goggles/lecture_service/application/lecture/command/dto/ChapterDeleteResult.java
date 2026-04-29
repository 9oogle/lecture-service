package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

public record ChapterDeleteResult(UUID lectureId, UUID chapterId) {

  public static ChapterDeleteResult from(UUID lectureId, UUID chapterId) {
    return new ChapterDeleteResult(lectureId, chapterId);
  }
}
