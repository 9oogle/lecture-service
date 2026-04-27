package com.goggles.lecture_service.domain.lecture;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class ChapterDuration {

  @Column(name = "duration_seconds", nullable = false)
  private int seconds;

  protected ChapterDuration() {}

  public ChapterDuration(int seconds) {
    if (seconds < 1)
      throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_DURATION_INVALID);
    this.seconds = seconds;
  }
}
