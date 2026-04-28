package com.goggles.lecture_service.domain.enrollment;

import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentErrorCode;
import com.goggles.lecture_service.domain.enrollment.exception.InvalidEnrollmentFieldException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureSnapshot {

  @Column(name = "lecture_id", nullable = false)
  private UUID lectureId;

  @Column(name = "lecture_title", nullable = false, length = 225)
  private String lectureTitle;

  @Column(name = "instructor_id", nullable = false)
  private UUID instructorId;

  @Column(name = "instructor_name", nullable = false, length = 100)
  private String instructorName;

  public static LectureSnapshot of(
      UUID lectureId, String lectureTitle, UUID instructorId, String instructorName) {
    validate(lectureId, lectureTitle, instructorId, instructorName);
    return new LectureSnapshot(lectureId, lectureTitle, instructorId, instructorName);
  }

  private LectureSnapshot(
      UUID lectureId, String lectureTitle, UUID instructorId, String instructorName) {
    this.lectureId = lectureId;
    this.lectureTitle = lectureTitle;
    this.instructorId = instructorId;
    this.instructorName = instructorName;
  }

  private static void validate(
      UUID lectureId, String lectureTitle, UUID instructorId, String instructorName) {
    if (lectureId == null)
      throw new InvalidEnrollmentFieldException(EnrollmentErrorCode.ENROLLMENT_LECTURE_ID_REQUIRED);
    if (lectureTitle == null || lectureTitle.isBlank())
      throw new InvalidEnrollmentFieldException(
          EnrollmentErrorCode.ENROLLMENT_LECTURE_TITLE_REQUIRED);
    if (instructorId == null)
      throw new InvalidEnrollmentFieldException(
          EnrollmentErrorCode.ENROLLMENT_INSTRUCTOR_ID_REQUIRED);
    if (instructorName == null || instructorName.isBlank())
      throw new InvalidEnrollmentFieldException(
          EnrollmentErrorCode.ENROLLMENT_INSTRUCTOR_NAME_REQUIRED);
  }
}
