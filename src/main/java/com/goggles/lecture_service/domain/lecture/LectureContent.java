package com.goggles.lecture_service.domain.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LectureContent {

  @Column(nullable = false, length = 200)
  private String title;

  @Column(length = 300)
  private String subtitle;

  @Column(columnDefinition = "TEXT")
  private String description;

  protected LectureContent() {}

  public LectureContent(String title, String subtitle, String description) {
    if (title == null || title.isBlank()) throw new IllegalArgumentException("강의 제목은 필수입니다.");
    if (title.length() > 200) throw new IllegalArgumentException("강의 제목은 200자 이하여야 합니다.");
    if (subtitle != null && subtitle.length() > 300)
      throw new IllegalArgumentException("강의 부제목은 300자 이하여야 합니다.");
    this.title = title;
    this.subtitle = subtitle;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public String getDescription() {
    return description;
  }
}
