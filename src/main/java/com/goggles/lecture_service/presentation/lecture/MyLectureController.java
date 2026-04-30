package com.goggles.lecture_service.presentation.lecture;

import com.goggles.common.pagination.CommonPageRequest;
import com.goggles.common.pagination.CommonPageResponse;
import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureQuery;
import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureResult;
import com.goggles.lecture_service.application.enrollment.query.service.EnrollmentQueryService;
import com.goggles.lecture_service.application.lecture.LectureService;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import com.goggles.lecture_service.presentation.lecture.dto.EnrolledLectureResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/lectures")
@RequiredArgsConstructor
public class MyLectureController {

  private final EnrollmentQueryService enrollmentQueryService;
  private final LectureService lectureService;

  // 나의 수강 강의 목록 조회 (학생) - ACTIVE, EXPIRED 조회
  @GetMapping("/enrolled")
  public CommonPageResponse<EnrolledLectureResponse> getEnrolledLectures(
      @RequestHeader("X-User-Id") UUID studentId,
      CommonPageRequest pageRequest,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String sort) {

    Page<EnrolledLectureResult> results =
        enrollmentQueryService.getEnrolledLectures(
            EnrolledLectureQuery.of(studentId, keyword, status, sort, pageRequest));

    return CommonPageResponse.of(results, EnrolledLectureResponse::from);
  }

  // 나의 강의 목록 조회 (강사) - 본인 소유 강의의 모든 상태 노출
  @GetMapping("/teaching")
  public CommonPageResponse<LectureSummary> getTeachingLectures(
      @RequestHeader("X-User-Id") UUID instructorId, CommonPageRequest pageRequest) {

    Page<LectureSummary> results = lectureService.getTeachingLectures(instructorId, pageRequest);

    return CommonPageResponse.of(results, lecture -> lecture);
  }
}
