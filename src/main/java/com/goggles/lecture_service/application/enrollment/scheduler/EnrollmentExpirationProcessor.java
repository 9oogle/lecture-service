package com.goggles.lecture_service.application.enrollment.scheduler;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 만료 대상 청크 처리 전용 컴포넌트. 스케줄러와 분리해 REQUIRES_NEW 트랜잭션이 AOP 프록시를 통해 정상 동작하도록 한다. */
@Component
@RequiredArgsConstructor
public class EnrollmentExpirationProcessor {

  private final EnrollmentRepository enrollmentRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int expireChunk(List<UUID> targetIds) {
    List<Enrollment> enrollments = enrollmentRepository.findAllByIdIn(targetIds);
    int expired = 0;
    for (Enrollment enrollment : enrollments) {
      if (!enrollment.isActive()) {
        continue;
      }
      enrollment.expire();
      expired++;
    }
    return expired;
  }
}
