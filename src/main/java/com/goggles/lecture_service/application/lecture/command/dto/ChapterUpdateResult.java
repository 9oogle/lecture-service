package com.goggles.lecture_service.application.lecture.command.dto;

import java.util.UUID;

public record ChapterUpdateResult(UUID lectureId, UUID chapterId) {

  public static ChapterUpdateResult from(UUID lectureId, UUID chapterId) {
    return new ChapterUpdateResult(lectureId, chapterId);
  }
}
