package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterCreateCommand;
import com.goggles.lecture_service.domain._common.UserType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChapterCreateRequest(
    @NotBlank String title,
    @NotBlank String content,
    @NotNull @Min(1) Integer sortOrder,
    @NotNull @Min(1) Integer durationSeconds) {

  public ChapterCreateCommand toCommand(UUID lectureId, UUID actorId, UserType actorRole) {
    return new ChapterCreateCommand(
        lectureId, actorId, actorRole, title, content, sortOrder, durationSeconds);
  }
}
