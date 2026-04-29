package com.goggles.lecture_service.presentation.lecture.dto;

import com.goggles.lecture_service.application.lecture.command.dto.ChapterReorderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record ChapterReorderRequest(
    @NotEmpty(message = "챕터 순서 변경 목록은 비어 있을 수 없습니다.")
        List<@Valid @NotNull ChapterOrderRequest> orders) {

  public ChapterReorderCommand toCommand(UUID lectureId, UUID actorId, String actorRole) {
    List<ChapterReorderCommand.ChapterOrderCommand> mappedOrders =
        orders == null
            ? null
            : orders.stream()
                .map(
                    order ->
                        order == null
                            ? null
                            : new ChapterReorderCommand.ChapterOrderCommand(
                                order.chapterId(), order.sortOrder()))
                .toList();

    return new ChapterReorderCommand(lectureId, actorId, actorRole, mappedOrders);
  }

  public record ChapterOrderRequest(
      @NotNull(message = "챕터 ID는 필수입니다.") UUID chapterId,
      @NotNull(message = "챕터 순서는 필수입니다.") @Min(value = 1, message = "챕터 순서는 1 이상이어야 합니다.")
          Integer sortOrder) {}
}
