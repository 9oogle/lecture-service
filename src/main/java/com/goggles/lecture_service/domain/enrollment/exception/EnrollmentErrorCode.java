package com.goggles.lecture_service.domain.enrollment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnrollmentErrorCode {

  // 조회
  ENROLLMENT_NOT_FOUND("해당 수강 정보를 찾을 수 없습니다."),

  // 중복
  ENROLLMENT_DUPLICATE("이미 수강 중인 강의입니다."),

  // 상태 전이
  ENROLLMENT_INVALID_STATUS_FOR_ACTIVATE("RESERVE 상태에서만 활성화할 수 있습니다."),
  ENROLLMENT_INVALID_STATUS_FOR_CANCEL("RESERVE 또는 ACTIVE 상태에서만 취소할 수 있습니다."),
  ENROLLMENT_INVALID_STATUS_FOR_EXPIRE("ACTIVE 상태에서만 만료 처리할 수 있습니다."),
  ENROLLMENT_INVALID_STATUS_FOR_ACCESS("ACTIVE 상태에서만 강의에 접근할 수 있습니다."),

  // 강의 검증
  ENROLLMENT_LECTURE_NOT_PUBLISHED("판매 중인 강의가 아닙니다."),
  ENROLLMENT_LECTURE_DELETED("삭제된 강의입니다."),
  ENROLLMENT_RESERVE_FAILED("수강 등록 예약에 실패했습니다."),
  ENROLLMENT_NOT_OWNED("본인 소유의 수강 정보가 아닙니다."),

  // 필수 필드 - 스냅샷
  ENROLLMENT_LECTURE_SNAPSHOT_REQUIRED("강의 스냅샷은 필수입니다."),
  ENROLLMENT_LECTURE_ID_REQUIRED("강의 ID는 필수입니다."),
  ENROLLMENT_LECTURE_TITLE_REQUIRED("강의명은 필수입니다."),
  ENROLLMENT_INSTRUCTOR_ID_REQUIRED("강사 ID는 필수입니다."),
  ENROLLMENT_INSTRUCTOR_NAME_REQUIRED("강사 이름은 필수입니다."),

  // 필수 필드 - 기타
  ENROLLMENT_STUDENT_ID_REQUIRED("학생 ID는 필수입니다."),
  ENROLLMENT_ORDER_ID_REQUIRED("주문 ID는 필수입니다."),
  ENROLLMENT_DURATION_POLICY_REQUIRED("수강 기간 정책은 필수입니다."),
  ENROLLMENT_TIME_REQUIRED("시간 정보는 필수입니다."),

  // 요청 필드 검증
  ENROLLMENT_PRODUCT_IDS_REQUIRED("상품 ID 목록은 필수입니다."),
  ENROLLMENT_PRODUCT_IDS_EMPTY("상품 ID 목록은 비어 있을 수 없습니다."),
  ENROLLMENT_USER_ID_REQUIRED("사용자 ID는 필수입니다."),
  ENROLLMENT_USER_NAME_REQUIRED("사용자 이름은 필수입니다.");

  private final String message;
}
