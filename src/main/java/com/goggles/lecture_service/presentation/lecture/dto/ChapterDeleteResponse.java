package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterDeleteResult;
import java.util.UUID;

public record ChapterDeleteResponse(UUID lectureId, UUID chapterId) {
  public static ChapterDeleteResponse from(ChapterDeleteResult result) {
    return new ChapterDeleteResponse(result.lectureId(), result.chapterId());
  }
}
