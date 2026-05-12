package com.goggles.lecture_service.presentation.enrollment.internal.dto;

import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentRollbackCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record LectureEnrollmentRollbackRequest(
    @NotEmpty(message = "enrollmentIds 는 비어있을 수 없습니다.")
        List<@NotNull(message = "enrollmentId 는 필수입니다.") UUID> enrollmentIds) {

  public LectureEnrollmentRollbackCommand toCommand(UUID userId) {
    return new LectureEnrollmentRollbackCommand(enrollmentIds, userId);
  }
}
