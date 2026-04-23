package com.goggles.lecture_service.infrastructure.lecture.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.goggles.lecture_service.domain.lecture.entity.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;
import com.goggles.lecture_service.domain.lecture.repository.LectureRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {

	private final LectureJpaRepository lectureJpaRepository;

	@Override
	public Lecture save(Lecture lecture) {
		return lectureJpaRepository.save(lecture);
	}

	@Override
	public Optional<Lecture> findById(UUID id) {
		return lectureJpaRepository.findById(id);
	}

	@Override
	public Optional<Lecture> findByIdAndDeletedAtIsNull(UUID id) {
		return lectureJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public List<Lecture> findAllByInstructorId(UUID instructorId) {
		return lectureJpaRepository.findAllByInstructorId(instructorId);
	}

	@Override

	public List<Lecture> findAllByStatusAndDeletedAtIsNull(LectureStatus status) {
		return lectureJpaRepository.findAllByStatusAndDeletedAtIsNull(status);
	}
}