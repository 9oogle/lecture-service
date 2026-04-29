package com.goggles.lecture_service.application.enrollment.command.service;

import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveCommand;
import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveResult;
import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.LectureSnapshot;
import com.goggles.lecture_service.domain.enrollment.enums.ReserveFailReason;
import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentReserveFailedException;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentCommandServiceImpl implements EnrollmentCommandService {

  private final LectureRepository lectureRepository;
  private final EnrollmentRepository enrollmentRepository;

  @Override
  public List<LectureEnrollmentReserveResult> reserve(LectureEnrollmentReserveCommand command) {
    // 1) 강의 일괄 조회
    List<Lecture> lectures = lectureRepository.findAllByIdIn(command.productIds());
    Map<UUID, Lecture> lectureMap =
        lectures.stream().collect(Collectors.toMap(Lecture::getId, l -> l));

    // 2) 전체 검증
    for (UUID productId : command.productIds()) {
      Lecture lecture = lectureMap.get(productId);

      if (lecture == null) {
        throw new EnrollmentReserveFailedException(productId, ReserveFailReason.LECTURE_NOT_FOUND);
      }
      if (!lecture.isOrderable()) {
        throw new EnrollmentReserveFailedException(productId, ReserveFailReason.NOT_PUBLISHED);
      }
      if (enrollmentRepository.existsActiveByStudentAndLecture(command.userId(), productId)) {
        throw new EnrollmentReserveFailedException(
            productId, ReserveFailReason.DUPLICATE_ENROLLMENT);
      }
    }
    // 3) 검증 통과 후 Enrollment 저장
    List<LectureEnrollmentReserveResult> results = new ArrayList<>();

    for (UUID productId : command.productIds()) {
      Lecture lecture = lectureMap.get(productId);

      LectureSnapshot snapshot =
          LectureSnapshot.of(
              lecture.getId(),
              lecture.getContent().getTitle(),
              lecture.getInstructor().getInstructorId(),
              lecture.getInstructor().getInstructorName());

      Enrollment enrollment =
          Enrollment.reserve(snapshot, command.userId(), lecture.getDurationPolicy());

      Enrollment saved = enrollmentRepository.save(enrollment);

      results.add(LectureEnrollmentReserveResult.of(saved, lecture));
    }

    return results;
  }
}
