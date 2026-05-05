package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterUpdateCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChapterUpdateRequest(
    @NotBlank(message = "챕터 제목은 필수입니다.") String title,
    @NotBlank(message = "챕터 내용은 필수입니다.") String content,
    @NotNull(message = "영상 길이는 필수입니다.") @Min(value = 1, message = "영상 길이는 1 이상이어야 합니다.")
        Integer durationSeconds) {

  public ChapterUpdateCommand toCommand(
      UUID lectureId, UUID chapterId, UUID actorId, String actorRole) {
    return new ChapterUpdateCommand(
        lectureId, chapterId, actorId, actorRole, title, content, durationSeconds);
  }
}
