package com.goggles.lecture_service.presentation.enrollment.internal.dto;

import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record LectureEnrollmentReserveRequest(
    @NotEmpty(message = "productIds 는 비어있을 수 없습니다.") List<UUID> productIds,
    @NotNull(message = "userId 는 필수입니다.") UUID userId,
    @NotBlank(message = "userName 은 필수입니다.") String userName) {

  public LectureEnrollmentReserveCommand toCommand() {
    return new LectureEnrollmentReserveCommand(productIds, userId, userName);
  }
}
