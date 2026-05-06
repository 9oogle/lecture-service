package com.goggles.lecture_service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.goggles.lecture_service.application.enrollment.scheduler.EnrollmentExpirationScheduler;
import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.LectureSnapshot;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import java.time.LocalDateTime;
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
@DisplayName("EnrollmentExpirationScheduler 테스트")
class EnrollmentExpirationSchedulerTest {

  @Mock private EnrollmentRepository enrollmentRepository;

  @InjectMocks private EnrollmentExpirationScheduler scheduler;

  private LectureSnapshot snapshot;

  @BeforeEach
  void setUp() {
    snapshot = LectureSnapshot.of(UUID.randomUUID(), "테스트 강의", UUID.randomUUID(), "테스트 강사");
  }

  /** ACTIVE 상태로 만들기 위한 헬퍼: reserve 후 activate. */
  private Enrollment activeEnrollment(DurationPolicy policy, LocalDateTime activatedAt) {
    Enrollment e = Enrollment.reserve(snapshot, UUID.randomUUID(), policy);
    e.activate(activatedAt, UUID.randomUUID());
    return e;
  }

  @Nested
  @DisplayName("expireEnrollments")
  class ExpireEnrollments {

    @Test
    @DisplayName("성공: 만료 대상 enrollment 가 EXPIRED 로 전환된다")
    void expireEnrollments_success() {
      // given
      Enrollment expired1 =
          activeEnrollment(DurationPolicy.DAYS_90, LocalDateTime.now().minusDays(91));
      Enrollment expired2 =
          activeEnrollment(DurationPolicy.DAYS_90, LocalDateTime.now().minusDays(100));
      List<UUID> targetIds = List.of(expired1.getId(), expired2.getId());

      when(enrollmentRepository.findExpirationTargetIds(any(LocalDateTime.class), anyInt()))
          .thenReturn(targetIds);
      when(enrollmentRepository.findAllByIdIn(targetIds)).thenReturn(List.of(expired1, expired2));

      // when
      scheduler.expireEnrollments();

      // then
      assertThat(expired1.getStatus()).isEqualTo(EnrollmentStatus.EXPIRED);
      assertThat(expired2.getStatus()).isEqualTo(EnrollmentStatus.EXPIRED);
    }

    @Test
    @DisplayName("성공: 만료 대상이 없으면 아무 처리도 하지 않는다")
    void expireEnrollments_noTargets() {
      // given
      when(enrollmentRepository.findExpirationTargetIds(any(LocalDateTime.class), anyInt()))
          .thenReturn(List.of());

      // when
      scheduler.expireEnrollments();

      // then
      verify(enrollmentRepository, never()).findAllByIdIn(any());
    }

    @Test
    @DisplayName("Race Condition: 조회 후 처리 사이에 ACTIVE 가 아닌 상태로 바뀐 건은 skip 한다")
    void expireEnrollments_skipNonActive() {
      // given
      Enrollment expiredActive =
          activeEnrollment(DurationPolicy.DAYS_90, LocalDateTime.now().minusDays(91));

      Enrollment canceledMidway =
          activeEnrollment(DurationPolicy.DAYS_90, LocalDateTime.now().minusDays(91));
      canceledMidway.cancel(); // 조회 후 취소된 상황 시뮬레이션

      List<UUID> targetIds = List.of(expiredActive.getId(), canceledMidway.getId());

      when(enrollmentRepository.findExpirationTargetIds(any(LocalDateTime.class), anyInt()))
          .thenReturn(targetIds);
      when(enrollmentRepository.findAllByIdIn(targetIds))
          .thenReturn(List.of(expiredActive, canceledMidway));

      // when
      scheduler.expireEnrollments();

      // then
      assertThat(expiredActive.getStatus()).isEqualTo(EnrollmentStatus.EXPIRED);
      assertThat(canceledMidway.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
    }
  }
}
