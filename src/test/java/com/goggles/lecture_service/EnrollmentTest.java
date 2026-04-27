package com.goggles.lecture_service;

import static org.assertj.core.api.Assertions.*;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.LectureSnapshot;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentStatusException;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EnrollmentTest {

  private LectureSnapshot lectureSnapshot;
  private UUID lectureId;
  private UUID instructorId;
  private UUID studentId;
  private UUID orderId;

  @BeforeEach
  void setUp() {
    lectureId = UUID.randomUUID();
    instructorId = UUID.randomUUID();
    studentId = UUID.randomUUID();
    orderId = UUID.randomUUID();

    lectureSnapshot = LectureSnapshot.of(lectureId, "스프링 부트 입문", instructorId, "홍길동");
  }

  @Nested
  @DisplayName("LectureSnapshot 생성")
  class SnapshotCreate {

    @Test
    @DisplayName("실패: lectureTitle 이 빈 문자열이면 예외")
    void snapshot_blankTitle_throws() {
      assertThatThrownBy(() -> LectureSnapshot.of(lectureId, "  ", instructorId, "홍길동"))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }

    @Test
    @DisplayName("실패: instructorName 이 null 이면 예외")
    void snapshot_nullInstructorName_throws() {
      assertThatThrownBy(() -> LectureSnapshot.of(lectureId, "스프링", instructorId, null))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }
  }

  @Nested
  @DisplayName("Enrollment 예약 (RESERVE)")
  class Reserve {

    @Test
    @DisplayName("성공: RESERVE 상태로 생성된다")
    void reserve_success() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);

      assertThat(enrollment).isNotNull();
      assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.RESERVE);
      assertThat(enrollment.getLectureId()).isEqualTo(lectureId);
      assertThat(enrollment.getInstructorId()).isEqualTo(instructorId);
      assertThat(enrollment.getLectureSnapshot().getLectureTitle()).isEqualTo("스프링 부트 입문");
      assertThat(enrollment.getStudentId()).isEqualTo(studentId);
      assertThat(enrollment.getOrderId()).isEqualTo(orderId);
      assertThat(enrollment.getActivatedAt()).isNull();
      assertThat(enrollment.getExpiresAt()).isNull();
      assertThat(enrollment.getLastAccessedAt()).isNull();
    }

    @Test
    @DisplayName("실패: lectureSnapshot 이 null 이면 예외")
    void reserve_nullSnapshot_throws() {
      assertThatThrownBy(
              () -> Enrollment.reserve(null, studentId, orderId, DurationPolicy.DAYS_365))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }

    @Test
    @DisplayName("실패: studentId 가 null 이면 예외")
    void reserve_nullStudentId_throws() {
      assertThatThrownBy(
              () -> Enrollment.reserve(lectureSnapshot, null, orderId, DurationPolicy.DAYS_365))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }

    @Test
    @DisplayName("실패: orderId 가 null 이면 예외")
    void reserve_nullOrderId_throws() {
      assertThatThrownBy(
              () -> Enrollment.reserve(lectureSnapshot, studentId, null, DurationPolicy.DAYS_365))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }

    @Test
    @DisplayName("실패: durationPolicy 가 null 이면 예외")
    void reserve_nullDurationPolicy_throws() {
      assertThatThrownBy(() -> Enrollment.reserve(lectureSnapshot, studentId, orderId, null))
          .isInstanceOf(InvalidEnrollmentFieldException.class);
    }
  }

  @Nested
  @DisplayName("Enrollment 활성화 (ACTIVE)")
  class Activate {

    @Test
    @DisplayName("성공: RESERVE → ACTIVE 전환되며 만료일이 설정된다")
    void activate_success() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);
      LocalDateTime now = LocalDateTime.now();

      enrollment.activate(now);

      assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
      assertThat(enrollment.getActivatedAt()).isEqualTo(now);
      assertThat(enrollment.getExpiresAt()).isEqualTo(now.plusDays(365));
    }

    @Test
    @DisplayName("성공: UNLIMITED 정책이면 만료일이 9999년으로 설정된다")
    void activate_unlimited_setsFarFuture() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.UNLIMITED);

      enrollment.activate(LocalDateTime.now());

      assertThat(enrollment.getExpiresAt().getYear()).isEqualTo(9999);
    }

    @Test
    @DisplayName("실패: RESERVE 가 아닌 상태에서 activate 하면 예외")
    void activate_notReserveStatus_throws() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);
      enrollment.cancel();

      assertThatThrownBy(() -> enrollment.activate(LocalDateTime.now()))
          .isInstanceOf(InvalidEnrollmentStatusException.class);
    }
  }

  @Nested
  @DisplayName("Enrollment 취소 (CANCEL)")
  class Cancel {

    @Test
    @DisplayName("성공: RESERVE 상태에서 취소 가능")
    void cancel_fromReserve_success() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);

      enrollment.cancel();

      assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
    }

    @Test
    @DisplayName("성공: ACTIVE 상태에서 취소 가능 (환불)")
    void cancel_fromActive_success() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);
      enrollment.activate(LocalDateTime.now());

      enrollment.cancel();

      assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
    }

    @Test
    @DisplayName("실패: 이미 CANCELED 인 enrollment 는 다시 취소 불가")
    void cancel_alreadyCanceled_throws() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);
      enrollment.cancel();

      assertThatThrownBy(enrollment::cancel).isInstanceOf(InvalidEnrollmentStatusException.class);
    }
  }

  @Nested
  @DisplayName("Enrollment 만료 (EXPIRE)")
  class Expire {

    @Test
    @DisplayName("성공: ACTIVE → EXPIRED 전환")
    void expire_fromActive_success() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);
      enrollment.activate(LocalDateTime.now());

      enrollment.expire();

      assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.EXPIRED);
    }

    @Test
    @DisplayName("실패: RESERVE 상태에서 expire 호출 시 예외")
    void expire_fromReserve_throws() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);

      assertThatThrownBy(enrollment::expire).isInstanceOf(InvalidEnrollmentStatusException.class);
    }
  }

  @Nested
  @DisplayName("최종 수강일시 (lastAccessedAt)")
  class TouchLastAccessed {

    @Test
    @DisplayName("성공: ACTIVE 상태에서 lastAccessedAt 갱신")
    void touch_fromActive_success() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);
      enrollment.activate(LocalDateTime.now());
      LocalDateTime accessTime = LocalDateTime.now();

      enrollment.touchLastAccessed(accessTime);

      assertThat(enrollment.getLastAccessedAt()).isEqualTo(accessTime);
    }

    @Test
    @DisplayName("실패: RESERVE 상태에서 호출 시 예외")
    void touch_fromReserve_throws() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);

      assertThatThrownBy(() -> enrollment.touchLastAccessed(LocalDateTime.now()))
          .isInstanceOf(InvalidEnrollmentStatusException.class);
    }

    @Test
    @DisplayName("실패: EXPIRED 상태에서 호출 시 예외")
    void touch_fromExpired_throws() {
      Enrollment enrollment =
          Enrollment.reserve(lectureSnapshot, studentId, orderId, DurationPolicy.DAYS_365);
      enrollment.activate(LocalDateTime.now());
      enrollment.expire();

      assertThatThrownBy(() -> enrollment.touchLastAccessed(LocalDateTime.now()))
          .isInstanceOf(InvalidEnrollmentStatusException.class);
    }
  }
}
