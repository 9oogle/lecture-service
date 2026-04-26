package com.goggles.lecture_service.domain.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ChapterDuration {

  @Column(name = "duration_seconds", nullable = false)
  private int seconds;

  protected ChapterDuration() {}

  public ChapterDuration(int seconds) {
    if (seconds < 0) throw new IllegalArgumentException("영상 길이는 0 이상이어야 합니다.");
    this.seconds = seconds;
  }

  public int getSeconds() {
    return seconds;
  }
}
