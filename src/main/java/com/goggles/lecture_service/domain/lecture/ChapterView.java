package com.goggles.lecture_service.domain.lecture;

import java.util.UUID;

public record ChapterView(
    UUID id, String title, String content, int sortOrder, int durationSeconds) {

  public static ChapterView from(Chapter chapter) {
    return new ChapterView(
        chapter.getId(),
        chapter.getContent().getTitle(),
        chapter.getContent().getContent(),
        chapter.getSortOrder(),
        chapter.getDuration().getSeconds());
  }
}
