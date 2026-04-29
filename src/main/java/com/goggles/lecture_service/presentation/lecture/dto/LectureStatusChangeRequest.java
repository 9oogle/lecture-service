package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureStatusChangeCommand;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record LectureStatusChangeRequest(
    @NotNull(message = "변경할 강의 상태는 필수입니다.") LectureStatus status,
    @Size(max = 1000, message = "반려 사유는 1000자 이하여야 합니다.") String rejectionReason) {

  public LectureStatusChangeCommand toCommand(UUID lectureId, UUID actorId, String actorRole) {
    return new LectureStatusChangeCommand(lectureId, actorId, actorRole, status, rejectionReason);
  }
}
