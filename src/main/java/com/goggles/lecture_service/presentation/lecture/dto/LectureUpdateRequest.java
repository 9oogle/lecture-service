package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.LectureUpdateCommand;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record LectureUpdateRequest(
    @NotBlank(message = "카테고리는 필수입니다.") @Size(max = 50, message = "카테고리는 50자 이하여야 합니다.")
        String category,
    @NotBlank(message = "제목은 필수입니다.") @Size(max = 200, message = "제목은 200자 이하여야 합니다.") String title,
    @Size(max = 300, message = "부제는 300자 이하여야 합니다.") String subtitle,
    String description,
    @NotNull(message = "수강 기간 정책은 필수입니다.") DurationPolicy durationPolicy,
    @NotNull(message = "가격은 필수입니다.") @Min(value = 0, message = "가격은 0원 이상이어야 합니다.") Long price) {

  public LectureUpdateCommand toCommand(UUID lectureId, UUID actorId, String actorRole) {
    return new LectureUpdateCommand(
        lectureId,
        actorId,
        actorRole,
        category,
        title,
        subtitle,
        description,
        durationPolicy,
        price);
  }
}
