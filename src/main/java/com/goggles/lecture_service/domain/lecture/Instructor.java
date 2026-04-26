package com.goggles.lecture_service.domain.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class Instructor {

  @Column(name = "instructor_id", nullable = false)
  private UUID instructorId;

  @Column(name = "instructor_name", nullable = false, length = 100)
  private String instructorName;

  protected Instructor() {}

  public Instructor(UUID instructorId, String instructorName) {
    if (instructorId == null) throw new IllegalArgumentException("강사 ID는 필수입니다.");
    if (instructorName == null || instructorName.isBlank())
      throw new IllegalArgumentException("강사 이름은 필수입니다.");
    if (instructorName.length() > 100) throw new IllegalArgumentException("강사 이름은 100자 이하여야 합니다.");
    this.instructorId = instructorId;
    this.instructorName = instructorName;
  }

  public UUID getInstructorId() {
    return instructorId;
  }

  public String getInstructorName() {
    return instructorName;
  }
}
