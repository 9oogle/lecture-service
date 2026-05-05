package com.goggles.lecture_service.application.lecture.query.dto;

import com.goggles.lecture_service.domain.lecture.ChapterView;
import java.util.UUID;

public record ChapterDetail(
    UUID id, String title, String content, int sortOrder, int durationSeconds) {

  public static ChapterDetail from(ChapterView view) {
    return new ChapterDetail(
        view.id(), view.title(), view.content(), view.sortOrder(), view.durationSeconds());
  }
}
