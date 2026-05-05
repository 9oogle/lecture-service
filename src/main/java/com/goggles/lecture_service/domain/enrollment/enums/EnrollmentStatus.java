package com.goggles.lecture_service.domain.enrollment.enums;

public enum EnrollmentStatus {
  RESERVE, // 주문 생성 시 예약, 결제 대기 중
  ACTIVE, // 결제 완료, 수강 가능
  CANCELED, // 주문 취소 또는 환불
  EXPIRED // 수강 기간 만료
}
