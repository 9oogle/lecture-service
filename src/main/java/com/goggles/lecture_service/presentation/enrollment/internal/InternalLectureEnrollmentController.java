package com.goggles.lecture_service.presentation.enrollment.internal;

import com.goggles.lecture_service.application.enrollment.command.service.EnrollmentCommandService;
import com.goggles.lecture_service.presentation.enrollment.internal.dto.LectureEnrollmentReserveRequest;
import com.goggles.lecture_service.presentation.enrollment.internal.dto.LectureEnrollmentReserveResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
      @Valid @RequestBody LectureEnrollmentReserveRequest request) {
    return enrollmentCommandService.reserve(request.toCommand()).stream()
        .map(LectureEnrollmentReserveResponse::from)
        .toList();
  }
}
