package com.goggles.lecture_service.presentation.enrollment.internal;

import com.goggles.lecture_service.application.enrollment.command.service.EnrollmentCommandService;
import com.goggles.lecture_service.presentation.enrollment.internal.dto.LectureEnrollmentCancelRequest;
import com.goggles.lecture_service.presentation.enrollment.internal.dto.LectureEnrollmentReserveRequest;
import com.goggles.lecture_service.presentation.enrollment.internal.dto.LectureEnrollmentReserveResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/lectures-enrollment")
@RequiredArgsConstructor
public class InternalLectureEnrollmentController {

  private final EnrollmentCommandService enrollmentCommandService;

  @PostMapping("/reserve")
  @ResponseStatus(HttpStatus.CREATED)
  public List<LectureEnrollmentReserveResponse> reserve(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @Valid @RequestBody LectureEnrollmentReserveRequest request) {

    return enrollmentCommandService.reserve(request.toCommand(userId)).stream()
        .map(LectureEnrollmentReserveResponse::from)
        .toList();
  }

  @PostMapping("/cancellation")
  @ResponseStatus(HttpStatus.OK)
  public void cancel(
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole,
      @Valid @RequestBody LectureEnrollmentCancelRequest request) {

    enrollmentCommandService.cancel(request.toCommand(userId));
  }
}
