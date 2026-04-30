package com.goggles.lecture_service.presentation.lecture;

import com.goggles.lecture_service.application.enrollment.query.dto.EnrolledLectureSummary;
import com.goggles.lecture_service.application.enrollment.query.service.EnrollmentQueryService;
import com.goggles.lecture_service.application.lecture.LectureService;
import com.goggles.lecture_service.application.lecture.query.dto.LectureSummary;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/lectures")
@RequiredArgsConstructor
public class MyLectureController {

  private final EnrollmentQueryService enrollmentQueryService;
  private final LectureService lectureService;

  // 나의 수강 강의 목록 조회 (학생) - ACTIVE 수강만 노출
  @GetMapping("/enrolled")
  public List<EnrolledLectureSummary> getEnrolledLectures(
      @RequestHeader("X-User-Id") UUID studentId) {
    return enrollmentQueryService.getEnrolledLectures(studentId);
  }

  // 나의 강의 목록 조회 (강사) - 본인 소유 강의의 모든 상태 노출
  @GetMapping("/teaching")
  public List<LectureSummary> getTeachingLectures(@RequestHeader("X-User-Id") UUID instructorId) {
    return lectureService.getTeachingLectures(instructorId);
  }
}
