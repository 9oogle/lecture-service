package com.goggles.lecture_service.application.enrollment.scheduler;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentExpirationScheduler {

  private static final int BATCH_LIMIT = 1000;

  private final EnrollmentRepository enrollmentRepository;

  @Scheduled(cron = "0 30 0 * * *", zone = "Asia/Seoul")
  @SchedulerLock(name = "enrollment-expiration", lockAtLeastFor = "PT1M", lockAtMostFor = "PT10M")
  @Transactional
  public void expireEnrollments() {
    LocalDateTime now = LocalDateTime.now();
    List<UUID> targetIds = enrollmentRepository.findExpirationTargetIds(now, BATCH_LIMIT);

    if (targetIds.isEmpty()) {
      log.debug("No enrollments to expire.");
      return;
    }

    List<Enrollment> enrollments = enrollmentRepository.findAllByIdIn(targetIds);
    int expiredCount = 0;
    for (Enrollment enrollment : enrollments) {
      if (!enrollment.isActive()) {
        continue;
      }
      enrollment.expire();
      expiredCount++;
    }

    log.info("Enrollment expiration completed. expired={}/{}", expiredCount, targetIds.size());
  }
}
