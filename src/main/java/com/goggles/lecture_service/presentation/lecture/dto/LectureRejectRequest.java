package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureRejectCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record LectureRejectRequest(
    @NotBlank(message = "반려 사유는 필수입니다.") @Size(max = 1000, message = "반려 사유는 1000자 이하여야 합니다.")
        String reason) {

  public LectureRejectCommand toCommand(UUID lectureId, UUID actorId, String actorRole) {
    return new LectureRejectCommand(lectureId, actorId, actorRole, reason);
  }
}
