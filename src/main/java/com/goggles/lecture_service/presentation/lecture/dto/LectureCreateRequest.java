package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureCreateCommand;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record LectureCreateRequest(
    @NotBlank(message = "카테고리는 필수입니다.") @Size(max = 50, message = "카테고리는 50자 이하여야 합니다.")
        String category,
    @NotBlank(message = "제목은 필수입니다.") @Size(max = 200, message = "제목은 200자 이하여야 합니다.") String title,
    @Size(max = 300, message = "부제는 300자 이하여야 합니다.") String subtitle,
    String description,
    DurationPolicy durationPolicy, // null 허용 - 도메인에서 DAYS_365 기본값 적용
    @NotNull(message = "가격은 필수입니다.") @Min(value = 0, message = "가격은 0원 이상이어야 합니다.") Long price) {

  public LectureCreateCommand toCommand(UUID instructorId, String instructorName) {
    return new LectureCreateCommand(
        instructorId,
        instructorName,
        category,
        title,
        subtitle,
        description,
        durationPolicy,
        price);
  }
}
