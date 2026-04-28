package com.goggles.lecture_service.domain.lecture;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.Getter;

@Embeddable
@Getter
public class Instructor {

  @Column(name = "instructor_id", nullable = false)
  private UUID instructorId;

  @Column(name = "instructor_name", nullable = false, length = 100)
  private String instructorName;

  protected Instructor() {}

  public Instructor(UUID instructorId, String instructorName) {
    if (instructorId == null)
      throw new InvalidLectureFieldException(LectureErrorCode.INSTRUCTOR_ID_REQUIRED);
    if (instructorName == null || instructorName.isBlank())
      throw new InvalidLectureFieldException(LectureErrorCode.INSTRUCTOR_NAME_REQUIRED);
    if (instructorName.length() > 100)
      throw new InvalidLectureFieldException(LectureErrorCode.INSTRUCTOR_NAME_TOO_LONG);
    this.instructorId = instructorId;
    this.instructorName = instructorName;
  }
}
