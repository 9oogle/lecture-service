package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChapterCreateRequest(
    @NotBlank String title,
    @NotBlank String content,
    @NotNull @Min(1) Integer sortOrder,
    @NotNull @Min(1) Integer durationSeconds) {
  public ChapterCreateCommand toCommand(UUID lectureId) {
    return new ChapterCreateCommand(lectureId, title, content, sortOrder, durationSeconds);
  }
}
