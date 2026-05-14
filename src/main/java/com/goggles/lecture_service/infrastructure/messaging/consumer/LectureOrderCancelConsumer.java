package com.goggles.lecture_service.infrastructure.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentCancelCommand;
import com.goggles.lecture_service.application.enrollment.command.service.EnrollmentCommandService;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidOrderCancelledEventPayloadException;
import com.goggles.lecture_service.infrastructure.messaging.event.LectureOrderCancelEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LectureOrderCancelConsumer {

  public static final String GROUP_NAME = "lecture-service.order-cancelled";

  private final EnrollmentCommandService enrollmentCommandService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "${topics.order.lecture-canceled}", groupId = GROUP_NAME)
  @IdempotentConsumer(GROUP_NAME)
  public void consume(ConsumerRecord<String, String> record) {
    log.info(
        "[Kafka] Received {} | partition={}, offset={}",
        record.topic(),
        record.partition(),
        record.offset());

    LectureOrderCancelEvent event = parse(record.value());

    log.info(
        "Processing order cancellation. orderId={}, enrollmentCount={}",
        event.orderId(),
        event.enrollmentIds().size());

    enrollmentCommandService.cancel(toCommand(event));
  }

  private LectureOrderCancelEvent parse(String value) {
    try {
      return objectMapper.readValue(value, LectureOrderCancelEvent.class);
    } catch (JsonProcessingException e) {
      throw new InvalidOrderCancelledEventPayloadException();
    }
  }

  private LectureEnrollmentCancelCommand toCommand(LectureOrderCancelEvent event) {
    try {
      return new LectureEnrollmentCancelCommand(event.enrollmentIds(), event.userId());
    } catch (InvalidEnrollmentFieldException e) {
      throw new InvalidOrderCancelledEventPayloadException();
    }
  }
}
