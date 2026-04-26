package com.goggles.lecture_service.domain.lecture;

import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;

public record LectureSearchCondition(
    String keyword,
    String category,
    Long minPrice,
    Long maxPrice,
    DurationPolicy durationPolicy,
    LectureStatus status) {
  public static LectureSearchCondition ofStudent(
      String keyword,
      String category,
      Long minPrice,
      Long maxPrice,
      DurationPolicy durationPolicy) {
    return new LectureSearchCondition(
        keyword, category, minPrice, maxPrice, durationPolicy, LectureStatus.PUBLISHED);
  }

  public static LectureSearchCondition ofAdmin(
      String keyword,
      String category,
      Long minPrice,
      Long maxPrice,
      DurationPolicy durationPolicy,
      LectureStatus status) {
    return new LectureSearchCondition(
        keyword, category, minPrice, maxPrice, durationPolicy, status);
  }
}
