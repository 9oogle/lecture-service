package com.goggles.lecture_service.domain.lecture;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class LectureContent {

  @Column(nullable = false, length = 200)
  private String title;

  @Column(length = 300)
  private String subtitle;

  @Column(columnDefinition = "TEXT")
  private String description;

  protected LectureContent() {}

  public LectureContent(String title, String subtitle, String description) {
    if (title == null || title.isBlank())
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_TITLE_REQUIRED);
    if (title.length() > 200)
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_TITLE_TOO_LONG);
    if (subtitle != null && subtitle.length() > 300)
      throw new InvalidLectureFieldException(LectureErrorCode.LECTURE_SUBTITLE_TOO_LONG);
    this.title = title;
    this.subtitle = subtitle;
    this.description = description;
  }
}
