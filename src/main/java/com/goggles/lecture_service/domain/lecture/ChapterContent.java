package com.goggles.lecture_service.domain.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ChapterContent {

  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  protected ChapterContent() {}

  public ChapterContent(String title, String content) {
    if (title == null || title.isBlank()) throw new IllegalArgumentException("챕터 제목은 필수입니다.");
    if (title.length() > 200) throw new IllegalArgumentException("챕터 제목은 200자 이하여야 합니다.");
    this.title = title;
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }
}
