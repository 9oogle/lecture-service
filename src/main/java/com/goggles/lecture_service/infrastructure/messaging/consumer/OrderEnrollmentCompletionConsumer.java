package com.goggles.lecture_service.infrastructure.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentCompleteCommand;
import com.goggles.lecture_service.application.enrollment.command.service.EnrollmentCommandService;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentCompletionEventPayloadException;
import com.goggles.lecture_service.infrastructure.messaging.event.OrderEnrollmentCompletionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEnrollmentCompletionConsumer {

  public static final String TOPIC = "order.enrollment.completion";
  public static final String GROUP_NAME = "lecture-service.enrollment-completion";

  private final EnrollmentCommandService enrollmentCommandService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = TOPIC, groupId = GROUP_NAME)
  @IdempotentConsumer(GROUP_NAME)
  public void consume(ConsumerRecord<String, String> record) {
    log.info(
        "[Kafka] Received {} | partition={}, offset={}",
        TOPIC,
        record.partition(),
        record.offset());

    OrderEnrollmentCompletionEvent event = parse(record.value());

    enrollmentCommandService.complete(
        new LectureEnrollmentCompleteCommand(event.orderId(), event.enrollmentIds()));
  }

  private OrderEnrollmentCompletionEvent parse(String value) {
    try {
      return objectMapper.readValue(value, OrderEnrollmentCompletionEvent.class);
    } catch (JsonProcessingException e) {
      // payload 가 깨진 경우
      throw new InvalidEnrollmentCompletionEventPayloadException();
    }
  }
}
