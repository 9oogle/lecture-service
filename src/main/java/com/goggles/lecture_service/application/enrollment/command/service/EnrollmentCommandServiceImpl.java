package com.goggles.lecture_service.application.enrollment.command.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentCancelCommand;
import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveCommand;
import com.goggles.lecture_service.application.enrollment.command.dto.LectureEnrollmentReserveResult;
import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.LectureSnapshot;
import com.goggles.lecture_service.domain.enrollment.enums.ReserveFailReason;
import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentErrorCode;
import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentNotFoundException;
import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentNotOwnedException;
import com.goggles.lecture_service.domain.enrollment.exception.EnrollmentReserveFailedException;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;
import com.goggles.lecture_service.domain.lecture.Lecture;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;

import lombok.RequiredArgsConstructor;

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

			lecture.validateOrderable();

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

	@Override
	public void cancel(LectureEnrollmentCancelCommand command) {
		List<Enrollment> enrollments = enrollmentRepository.findAllByIdIn(command.enrollmentIds());

		Map<UUID, Enrollment> enrollmentMap =
			enrollments.stream().collect(Collectors.toMap(Enrollment::getId, enrollment -> enrollment));

		for (UUID enrollmentId : command.enrollmentIds()) {
			Enrollment enrollment = enrollmentMap.get(enrollmentId);

			if (enrollment == null) {
				throw new EnrollmentNotFoundException(enrollmentId);
			}

			if (!enrollment.getStudentId().equals(command.userId())) {
				throw new EnrollmentNotOwnedException(EnrollmentErrorCode.ENROLLMENT_NOT_OWNED);
			}
		}

		for (UUID enrollmentId : command.enrollmentIds()) {
			enrollmentMap.get(enrollmentId).cancel();
		}
	}
}
