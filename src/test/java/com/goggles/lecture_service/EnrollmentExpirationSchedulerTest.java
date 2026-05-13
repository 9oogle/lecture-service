package com.goggles.lecture_service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.goggles.lecture_service.application.enrollment.scheduler.EnrollmentExpirationProcessor;
import com.goggles.lecture_service.application.enrollment.scheduler.EnrollmentExpirationScheduler;
import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.LectureSnapshot;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnrollmentExpirationSchedulerTest {

  @Mock private EnrollmentRepository enrollmentRepository;
  @Mock private EnrollmentExpirationProcessor processor;

  @InjectMocks private EnrollmentExpirationScheduler scheduler;

  // ACTIVE 상태의 Enrollment 생성 헬퍼
  private Enrollment activeEnrollment(DurationPolicy policy, LocalDateTime activatedAt) {
    LectureSnapshot snapshot =
        LectureSnapshot.of(UUID.randomUUID(), "강의명", UUID.randomUUID(), "강사명");
    Enrollment enrollment = Enrollment.reserve(snapshot, UUID.randomUUID(), policy);
    enrollment.activate(activatedAt, UUID.randomUUID());
    return enrollment;
  }

  @Nested
  @DisplayName("expireEnrollments (스케줄러 위임)")
  class ExpireEnrollments {

    @Test
    @DisplayName("성공: 만료 대상이 청크 크기 미만이면 한 번만 processor에 위임한다")
    void expireEnrollments_singleChunk() {
      // given
      List<UUID> targetIds = List.of(UUID.randomUUID(), UUID.randomUUID());

      when(enrollmentRepository.findExpirationTargetIds(any(LocalDateTime.class), anyInt()))
          .thenReturn(targetIds);
      when(processor.expireChunk(targetIds)).thenReturn(2);

      // when
      scheduler.expireEnrollments();

      // then
      verify(enrollmentRepository, times(1))
          .findExpirationTargetIds(any(LocalDateTime.class), anyInt());
      verify(processor, times(1)).expireChunk(targetIds);
    }

    @Test
    @DisplayName("성공: 만료 대상이 없으면 processor 호출 없이 종료")
    void expireEnrollments_noTargets() {
      // given
      when(enrollmentRepository.findExpirationTargetIds(any(LocalDateTime.class), anyInt()))
          .thenReturn(List.of());

      // when
      scheduler.expireEnrollments();

      // then
      verify(processor, never()).expireChunk(any());
    }

    @Test
    @DisplayName("성공: 청크가 한도와 같으면 다음 청크를 추가 조회한다")
    void expireEnrollments_multipleChunks() {
      // given
      List<UUID> fullChunk = generateIds(200);
      List<UUID> tail = List.of(UUID.randomUUID());

      when(enrollmentRepository.findExpirationTargetIds(any(LocalDateTime.class), anyInt()))
          .thenReturn(fullChunk)
          .thenReturn(tail);
      when(processor.expireChunk(any())).thenReturn(0);

      // when
      scheduler.expireEnrollments();

      // then - 가득 찬 청크 1번 + 마지막 청크 1번 = 2번 호출
      verify(enrollmentRepository, times(2))
          .findExpirationTargetIds(any(LocalDateTime.class), anyInt());
      verify(processor, times(2)).expireChunk(any());
    }
  }

  @Nested
  @DisplayName("EnrollmentExpirationProcessor.expireChunk (도메인 동작)")
  class ProcessChunk {

    private EnrollmentExpirationProcessor realProcessor;

    @BeforeEach
    void setUp() {
      realProcessor = new EnrollmentExpirationProcessor(enrollmentRepository);
    }

    @Test
    @DisplayName("성공: ACTIVE 만료 대상이 EXPIRED로 전환된다")
    void expireChunk_success() {
      // given
      Enrollment expired1 =
          activeEnrollment(DurationPolicy.DAYS_90, LocalDateTime.now().minusDays(91));
      Enrollment expired2 =
          activeEnrollment(DurationPolicy.DAYS_90, LocalDateTime.now().minusDays(100));
      List<UUID> targetIds = List.of(expired1.getId(), expired2.getId());

      when(enrollmentRepository.findAllByIdIn(targetIds)).thenReturn(List.of(expired1, expired2));

      // when
      int expired = realProcessor.expireChunk(targetIds);

      // then
      assertThat(expired).isEqualTo(2);
      assertThat(expired1.getStatus()).isEqualTo(EnrollmentStatus.EXPIRED);
      assertThat(expired2.getStatus()).isEqualTo(EnrollmentStatus.EXPIRED);
    }

    @Test
    @DisplayName("Race Condition: 조회 후 ACTIVE가 아닌 건은 skip한다")
    void expireChunk_skipNonActive() {
      // given
      Enrollment expiredActive =
          activeEnrollment(DurationPolicy.DAYS_90, LocalDateTime.now().minusDays(91));
      // 조회 직후 다른 트랜잭션이 먼저 EXPIRED로 만들어버린 케이스
      Enrollment alreadyExpired =
          activeEnrollment(DurationPolicy.DAYS_90, LocalDateTime.now().minusDays(91));
      alreadyExpired.expire();

      List<UUID> targetIds = List.of(expiredActive.getId(), alreadyExpired.getId());

      when(enrollmentRepository.findAllByIdIn(targetIds))
          .thenReturn(List.of(expiredActive, alreadyExpired));

      // when
      int expired = realProcessor.expireChunk(targetIds);

      // then
      assertThat(expired).isEqualTo(1);
      assertThat(expiredActive.getStatus()).isEqualTo(EnrollmentStatus.EXPIRED);
      assertThat(alreadyExpired.getStatus()).isEqualTo(EnrollmentStatus.EXPIRED);
    }
  }

  private static List<UUID> generateIds(int n) {
    List<UUID> ids = new ArrayList<>(n);
    for (int i = 0; i < n; i++) ids.add(UUID.randomUUID());
    return ids;
  }
}
