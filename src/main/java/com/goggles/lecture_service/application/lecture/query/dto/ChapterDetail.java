package com.goggles.lecture_service.application.lecture.query.dto;

import com.goggles.lecture_service.domain.lecture.Chapter;
import java.util.UUID;

public record ChapterDetail(
    UUID id, String title, String content, int sortOrder, int durationSeconds) {

  public static ChapterDetail from(Chapter chapter) {
    return new ChapterDetail(
        chapter.getId(),
        chapter.getContent().getTitle(),
        chapter.getContent().getContent(),
        chapter.getSortOrder(),
        chapter.getDuration().getSeconds());
  }
}
