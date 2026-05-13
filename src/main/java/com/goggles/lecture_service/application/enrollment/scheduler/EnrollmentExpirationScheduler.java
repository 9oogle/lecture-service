package com.goggles.lecture_service.application.enrollment.scheduler;

import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnrollmentExpirationScheduler {

  // 한 트랜잭션이 처리하는 단위. 1000건 dirty checking flush 비용을 줄이기 위해 작게 유지.
  private static final int CHUNK_SIZE = 200;
  // 한 스케줄 실행이 처리하는 최대 청크 수 (안전장치). 평소엔 닿지 않음.
  private static final int MAX_CHUNKS_PER_RUN = 50;

  private final EnrollmentRepository enrollmentRepository;
  private final EnrollmentExpirationProcessor processor;

  @Scheduled(cron = "0 30 0 * * *", zone = "Asia/Seoul")
  @SchedulerLock(name = "enrollment-expiration", lockAtLeastFor = "PT1M", lockAtMostFor = "PT30M")
  public void expireEnrollments() {
    LocalDateTime now = LocalDateTime.now();
    int totalExpired = 0;
    int totalScanned = 0;

    for (int chunk = 0; chunk < MAX_CHUNKS_PER_RUN; chunk++) {
      List<UUID> targetIds = enrollmentRepository.findExpirationTargetIds(now, CHUNK_SIZE);
      if (targetIds.isEmpty()) {
        break;
      }
      totalScanned += targetIds.size();
      // 각 청크를 독립 트랜잭션으로 처리 → 영속성 컨텍스트 폭증/긴 lock 방지
      totalExpired += processor.expireChunk(targetIds);

      // 한 번에 가져온 청크가 한도 미만이면 더 이상 만료 대상 없음
      if (targetIds.size() < CHUNK_SIZE) {
        break;
      }
    }

    log.info(
        "Enrollment expiration completed. expired={}/{} (chunkSize={})",
        totalExpired,
        totalScanned,
        CHUNK_SIZE);
  }
}
