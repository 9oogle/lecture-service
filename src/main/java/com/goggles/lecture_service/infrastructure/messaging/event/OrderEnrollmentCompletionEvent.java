package com.goggles.lecture_service.infrastructure.messaging.event;

import java.util.List;
import java.util.UUID;

public record OrderEnrollmentCompletionEvent(UUID orderId, UUID userId, List<UUID> enrollmentIds) {

  public OrderEnrollmentCompletionEvent {
    enrollmentIds = enrollmentIds == null ? List.of() : List.copyOf(enrollmentIds);
  }
}
