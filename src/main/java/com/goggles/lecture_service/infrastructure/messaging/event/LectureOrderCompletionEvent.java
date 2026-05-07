package com.goggles.lecture_service.infrastructure.messaging.event;

import java.util.List;
import java.util.UUID;

public record LectureOrderCompletionEvent(UUID orderId, UUID userId, List<UUID> enrollmentIds) {

  public LectureOrderCompletionEvent {
    enrollmentIds = enrollmentIds == null ? List.of() : List.copyOf(enrollmentIds);
  }
}
