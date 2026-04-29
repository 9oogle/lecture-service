package com.goggles.lecture_service.presentation.enrollment.internal.dto;

import java.util.List;
import java.util.UUID;

import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentCancelCommand;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LectureEnrollmentCancelRequest(
	@NotEmpty(message = "enrollmentIds 는 비어있을 수 없습니다.")
	List<@NotNull(message = "enrollmentId 는 필수입니다.") UUID> enrollmentIds,
	@NotNull(message = "userId 는 필수입니다.") UUID userId) {

	public LectureEnrollmentCancelCommand toCommand() {
		return new LectureEnrollmentCancelCommand(enrollmentIds, userId);
	}
}
