package com.goggles.lecture_service.domain.lecture.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LectureErrorCode {

  // 강의
  LECTURE_NOT_FOUND("해당 강의를 찾을 수 없습니다."),
  LECTURE_INVALID_STATUS("현재 상태에서 허용되지 않는 작업입니다."),
  LECTURE_CHAPTER_REQUIRED("챕터가 최소 1개 이상 있어야 합니다."),
  LECTURE_CATEGORY_REQUIRED("카테고리는 필수입니다."),
  LECTURE_REJECTION_REASON_REQUIRED("반려 사유는 필수입니다."),

  // 챕터
  CHAPTER_NOT_FOUND("해당 챕터를 찾을 수 없습니다."),
  CHAPTER_DUPLICATE_SORT_ORDER("이미 존재하는 챕터 순서입니다."),
  INVALID_SORT_ORDER("챕터 순서는 1 이상이어야 합니다.");

  private final String message;
}
