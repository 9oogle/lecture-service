package com.goggles.lecture_service.domain.lecture.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LectureErrorCode {

  // 강의
  LECTURE_NOT_FOUND("해당 강의를 찾을 수 없습니다."),
  LECTURE_ID_REQUIRED("강의 ID는 필수입니다."),
  LECTURE_ACCESS_DENIED("강의에 대한 접근 권한이 없습니다."),
  LECTURE_INVALID_STATUS("현재 상태에서 허용되지 않는 작업입니다."),
  LECTURE_CHAPTER_REQUIRED("챕터가 최소 1개 이상 있어야 합니다."),
  LECTURE_CATEGORY_REQUIRED("카테고리는 필수입니다."),
  LECTURE_STATUS_REQUIRED("변경할 강의 상태는 필수입니다."),
  LECTURE_REJECTION_REASON_REQUIRED("반려 사유는 필수입니다."),
  LECTURE_REJECTION_REASON_TOO_LONG("반려 사유는 1000자 이하여야 합니다."),
  LECTURE_TITLE_REQUIRED("강의 제목은 필수입니다."),
  LECTURE_TITLE_TOO_LONG("강의 제목은 200자 이하여야 합니다."),
  LECTURE_SUBTITLE_TOO_LONG("강의 부제목은 300자 이하여야 합니다."),

  // 요청 필드 검증
  USER_ID_REQUIRED("사용자 ID는 필수입니다."),
  USER_ROLE_REQUIRED("사용자 권한은 필수입니다."),
  LECTURE_DURATION_POLICY_REQUIRED("수강 기간 정책은 필수입니다."),

  // 챕터
  CHAPTER_NOT_FOUND("해당 챕터를 찾을 수 없습니다."),
  CHAPTER_ID_REQUIRED("챕터 ID는 필수입니다."),
  CHAPTER_DUPLICATE_SORT_ORDER("이미 존재하는 챕터 순서입니다."),
  CHAPTER_INVALID_SORT_ORDER("챕터 순서는 1 이상이어야 합니다."),
  CHAPTER_REORDER_ITEMS_REQUIRED("챕터 순서 변경 목록은 필수입니다."),
  CHAPTER_REORDER_ITEMS_EMPTY("챕터 순서 변경 목록은 비어 있을 수 없습니다."),
  CHAPTER_SORT_ORDER_REQUIRED("챕터 순서는 필수입니다."),
  CHAPTER_ID_DUPLICATED("챕터 ID가 중복될 수 없습니다."),
  CHAPTER_TITLE_REQUIRED("챕터 제목은 필수입니다."),
  CHAPTER_TITLE_TOO_LONG("챕터 제목은 200자 이하여야 합니다."),
  CHAPTER_DURATION_INVALID("영상 길이는 1 이상이어야 합니다."),
  CHAPTER_LECTURE_REQUIRED("챕터의 강의 정보는 필수입니다."),
  CHAPTER_CONTENT_REQUIRED("챕터 내용은 필수입니다."),
  CHAPTER_DURATION_REQUIRED("챕터 영상 정보는 필수입니다."),

  // 가격
  PRICE_REQUIRED("가격은 필수입니다."),
  PRICE_NEGATIVE("가격은 0원 이상이어야 합니다."),

  // 강사
  INSTRUCTOR_ID_REQUIRED("강사 ID는 필수입니다."),
  INSTRUCTOR_NAME_REQUIRED("강사 이름은 필수입니다."),
  INSTRUCTOR_NAME_TOO_LONG("강사 이름은 100자 이하여야 합니다.");

  private final String message;
}
