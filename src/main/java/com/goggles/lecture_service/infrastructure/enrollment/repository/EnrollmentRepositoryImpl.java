package com.goggles.lecture_service.infrastructure.enrollment.repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.goggles.lecture_service.domain.enrollment.Enrollment;
import com.goggles.lecture_service.domain.enrollment.enums.EnrollmentStatus;
import com.goggles.lecture_service.domain.enrollment.repository.EnrollmentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

	private static final Set<EnrollmentStatus> ACTIVE_OR_RESERVED =
		EnumSet.of(EnrollmentStatus.RESERVE, EnrollmentStatus.ACTIVE);

	private final EnrollmentJpaRepository jpaRepository;

	@Override
	public Enrollment save(Enrollment enrollment) {
		return jpaRepository.save(enrollment);
	}

	@Override
	public Optional<Enrollment> findById(UUID id) {
		return jpaRepository.findById(id);
	}

	@Override
	public List<Enrollment> findAllByIdIn(List<UUID> ids) {
		return jpaRepository.findAllByIdIn(ids);
	}

	@Override
	public Optional<Enrollment> findByOrderIdAndLectureId(UUID orderId, UUID lectureId) {
		return jpaRepository.findByOrderIdAndLectureId(orderId, lectureId);
	}

	@Override
	public boolean existsActiveByStudentAndLecture(UUID studentId, UUID lectureId) {
		return jpaRepository.existsByStudentAndLectureAndStatusIn(
			studentId, lectureId, ACTIVE_OR_RESERVED);
	}

	@Override
	public List<Enrollment> findAllByOrderId(UUID orderId) {
		return jpaRepository.findAllByOrderId(orderId);
	}

	@Override
	public List<Enrollment> findActiveByStudentId(UUID studentId) {
		return jpaRepository.findByStudentIdAndStatus(studentId, EnrollmentStatus.ACTIVE);
	}
}
