package com.goggles.lecture_service.domain.lecture;

import com.goggles.lecture_service.domain.lecture.exception.InvalidLectureFieldException;
import com.goggles.lecture_service.domain.lecture.exception.LectureErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class ChapterContent {

  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  protected ChapterContent() {}

  public ChapterContent(String title, String content) {
    if (title == null || title.isBlank())
      throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_TITLE_REQUIRED);
    if (title.length() > 200)
      throw new InvalidLectureFieldException(LectureErrorCode.CHAPTER_TITLE_TOO_LONG);
    this.title = title;
    this.content = content;
  }
}
