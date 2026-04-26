package com.goggles.lecture_service.application.lecture.query.dto;

import com.goggles.lecture_service.domain.lecture.LectureSearchCondition;
import com.goggles.lecture_service.domain.lecture.enums.DurationPolicy;

public record LectureListQuery(
    String keyword, String category, Long minPrice, Long maxPrice, DurationPolicy durationPolicy) {

  public LectureSearchCondition toCondition() {
    return LectureSearchCondition.ofStudent(keyword, category, minPrice, maxPrice, durationPolicy);
  }
}
