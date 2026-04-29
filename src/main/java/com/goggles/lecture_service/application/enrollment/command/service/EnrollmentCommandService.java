package com.goggles.lecture_service.application.enrollment.command.service;

import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentCancelCommand;
import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveCommand;
import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveResult;
import java.util.List;

public interface EnrollmentCommandService {

  List<LectureEnrollmentReserveResult> reserve(LectureEnrollmentReserveCommand command);

  void cancel(LectureEnrollmentCancelCommand command);
}
