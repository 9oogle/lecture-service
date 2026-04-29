package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterUpdateResult;
import java.util.UUID;

public record ChapterUpdateResponse(UUID lectureId, UUID chapterId) {
  public static ChapterUpdateResponse from(ChapterUpdateResult result) {
    return new ChapterUpdateResponse(result.lectureId(), result.chapterId());
  }
}
