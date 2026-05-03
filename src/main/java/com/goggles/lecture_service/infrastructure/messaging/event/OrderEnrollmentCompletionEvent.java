package com.goggles.lecture_service.infrastructure.messaging.event;

import java.util.List;
import java.util.UUID;

/**
 * 주문 서비스가 발행하는 수강 등록 완료 이벤트.
 *
 * <p>결제 서비스가 결제 성공을 알리면, 주문 서비스가 자기 상태를 결제완료로 정리한 뒤 이 이벤트를 발행한다. 강의 서비스(본 서비스)는 이 이벤트를 수신해
 * enrollment 를 RESERVE → ACTIVE 로 전환한다.
 *
 * <p>Topic : {@code order.enrollment.completion}
 *
 * <p>예시 payload :
 *
 * <pre>{@code
 * {
 *   "orderId": "5fa1c7b0-...",
 *   "enrollmentIds": ["8b2c...", "9d4e..."]
 * }
 * }</pre>
 *
 * <p>Kafka header 의 {@code message_id} (UUID) 는 {@code @IdempotentConsumer} 가 멱등성 키로 사용한다.
 */
public record OrderEnrollmentCompletionEvent(UUID orderId, List<UUID> enrollmentIds) {

  public OrderEnrollmentCompletionEvent {
    enrollmentIds = enrollmentIds == null ? List.of() : List.copyOf(enrollmentIds);
  }
}
