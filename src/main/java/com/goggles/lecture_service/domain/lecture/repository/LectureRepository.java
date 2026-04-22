package com.goggles.lecture_service.domain.lecture.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.goggles.lecture_service.domain.lecture.entity.Lecture;
import com.goggles.lecture_service.domain.lecture.enums.LectureStatus;

public interface LectureRepository {
	Lecture save(Lecture lecture);

	Optional<Lecture> findById(UUID id);

	Optional<Lecture> findByIdAndDeletedAtIsNull(UUID id);

	List<Lecture> findAllByInstructorInstructorId(UUID instructorId);

	List<Lecture> findAllByStatus(LectureStatus status);

}
