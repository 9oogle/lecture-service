package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterReorderResult;
import java.util.UUID;

public record ChapterReorderResponse(UUID lectureId) {
  public static ChapterReorderResponse from(ChapterReorderResult result) {
    return new ChapterReorderResponse(result.lectureId());
  }
}
