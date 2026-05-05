package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateResult;
import java.util.UUID;

public record ChapterCreateResponse(UUID lectureId, UUID chapterId) {
  public static ChapterCreateResponse from(ChapterCreateResult result) {
    return new ChapterCreateResponse(result.lectureId(), result.chapterId());
  }
}
