package com.goggles.lecture_service.domain.enrollment;

import com.goggles.common.domain.BaseAudit;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentErrorCode;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentStatusException;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
    name = "p_enrollment",
    indexes = {
      @Index(name = "idx_enrollment_student_status", columnList = "student_id, status"),
      @Index(name = "idx_enrollment_order_id", columnList = "order_id")
    })
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment extends BaseAudit {

  @Id
  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id = UUID.randomUUID();

  // 강의 스냅샷 (lectureId + lectureTitle + instructorId + instructorName)
  @Embedded private LectureSnapshot lectureSnapshot;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "order_id")
  private UUID orderId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private EnrollmentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "duration_policy", nullable = false, length = 20)
  private DurationPolicy durationPolicy;

  @Column(name = "activated_at")
  private LocalDateTime activatedAt;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "last_accessed_at")
  private LocalDateTime lastAccessedAt; // ← 추가

  // 정적 팩토리 메서드: RESERVE 로 생성
  public static Enrollment reserve(
      LectureSnapshot snapshot, UUID studentId, DurationPolicy durationPolicy) {

    if (snapshot == null) {
      throw new InvalidEnrollmentFieldException(
          EnrollmentErrorCode.ENROLLMENT_LECTURE_SNAPSHOT_REQUIRED);
    }

    if (studentId == null) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_STUDENT_ID_REQUIRED);
    }

    if (durationPolicy == null) {
      throw new InvalidEnrollmentFieldException(
          EnrollmentErrorCode.ENROLLMENT_DURATION_POLICY_REQUIRED);
    }

    return new Enrollment(snapshot, studentId, durationPolicy);
  }

  private Enrollment(
      LectureSnapshot lectureSnapshot, UUID studentId, DurationPolicy durationPolicy) {
    this.lectureSnapshot = lectureSnapshot;
    this.studentId = studentId;
    this.durationPolicy = durationPolicy;
    this.status = EnrollmentStatus.RESERVE;
  }

  // 도메인 메서드 (상태 전이)
  public void activate(LocalDateTime now, UUID orderId) {
    validateNow(now);
    validateOrderId(orderId);
    if (this.status != EnrollmentStatus.RESERVE) {
      throw new InvalidEnrollmentStatusException(
          EnrollmentErrorCode.ENROLLMENT_INVALID_STATUS_FOR_ACTIVATE);
    }
    this.activatedAt = now;
    this.orderId = orderId;
    this.expiresAt = calculateExpiresAt(now, this.durationPolicy);
    this.status = EnrollmentStatus.ACTIVE;
  }

  public void cancel() {
    if (this.status != EnrollmentStatus.RESERVE && this.status != EnrollmentStatus.ACTIVE) {
      throw new InvalidEnrollmentStatusException(
          EnrollmentErrorCode.ENROLLMENT_INVALID_STATUS_FOR_CANCEL);
    }
    this.status = EnrollmentStatus.CANCELED;
  }

  public void expire() {
    if (this.status != EnrollmentStatus.ACTIVE) {
      throw new InvalidEnrollmentStatusException(
          EnrollmentErrorCode.ENROLLMENT_INVALID_STATUS_FOR_EXPIRE);
    }
    this.status = EnrollmentStatus.EXPIRED;
  }

  // 사용자가 강의에 접근할 때 호출 (마지막 수강 시각 갱신)
  public void touchLastAccessed(LocalDateTime now) {
    validateNow(now);
    if (this.status != EnrollmentStatus.ACTIVE) {
      throw new InvalidEnrollmentStatusException(
          EnrollmentErrorCode.ENROLLMENT_INVALID_STATUS_FOR_ACCESS);
    }
    this.lastAccessedAt = now;
  }

  // 편의 메서드
  public boolean isActive() {
    return this.status == EnrollmentStatus.ACTIVE;
  }

  public boolean isExpired(LocalDateTime now) {
    validateNow(now);
    return this.expiresAt != null && now.isAfter(this.expiresAt);
  }

  // 스냅샷 내부 직접 접근
  public UUID getLectureId() {
    return this.lectureSnapshot.getLectureId();
  }

  public UUID getInstructorId() {
    return this.lectureSnapshot.getInstructorId();
  }

  // 내부 검증
  private static void validateRequired(
      LectureSnapshot lectureSnapshot, UUID studentId, DurationPolicy durationPolicy) {
    if (lectureSnapshot == null)
      throw new InvalidEnrollmentFieldException(
          EnrollmentErrorCode.ENROLLMENT_LECTURE_SNAPSHOT_REQUIRED);
    if (studentId == null)
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_STUDENT_ID_REQUIRED);
    if (durationPolicy == null)
      throw new InvalidEnrollmentFieldException(
          EnrollmentErrorCode.ENROLLMENT_DURATION_POLICY_REQUIRED);
  }

  private static void validateOrderId(UUID orderId) {
    if (orderId == null) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_ORDER_ID_REQUIRED);
    }
  }

  private static void validateNow(LocalDateTime now) {
    if (now == null) {
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_TIME_REQUIRED);
    }
  }

  private static LocalDateTime calculateExpiresAt(LocalDateTime from, DurationPolicy policy) {
    return switch (policy) {
      case DAYS_90 -> from.plusDays(90);
      case DAYS_180 -> from.plusDays(180);
      case DAYS_365 -> from.plusDays(365);
      case UNLIMITED -> LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    };
  }
}
