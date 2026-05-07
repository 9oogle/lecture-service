package com.goggles.lecture_service.infrastructure.messaging.event;

import java.util.List;
import java.util.UUID;

public record OrderCancelledEvent(
    UUID orderId,
    UUID userId,
    List<UUID> enrollmentIds,
    String cancelReason,
    String cancelDescription) {

  public OrderCancelledEvent {
    enrollmentIds = enrollmentIds == null ? List.of() : List.copyOf(enrollmentIds);
  }
}
